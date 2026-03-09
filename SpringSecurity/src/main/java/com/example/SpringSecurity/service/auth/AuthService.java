package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dto.request.auth.LogoutRequest;
import com.example.SpringSecurity.dto.request.auth.ResetPasswordUserRequest;
import com.example.SpringSecurity.dto.response.auth.LoginResponse;
import com.example.SpringSecurity.dto.request.auth.LoginUserRequest;
import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.repository.IVerifyOTPRepository;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.IRedisService;
import com.example.SpringSecurity.service.email.IEmaiService;
import com.example.SpringSecurity.service.historyLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.IJwtService;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService{

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final IHistoryLoginService historyLoginService;
    private final IVerifyOTPRepository verifyOTPRepository;
    private final IEmaiService emaiService;
    private final IUserValidationService userValidationService;
    private final IRedisService redisService;

    @Override
    @Transactional
    @CacheEvict(value = "users_list", allEntries = true)
    public ApiResponse<User> signup(RegisterUserRequest registerUser) {
        log.info("Starting signup process for email={}", registerUser.getEmail());
        if(userValidationService.findByEmail(registerUser.getEmail())) {
            log.debug("Signup failed because email already exists: email={}", registerUser.getEmail());
            return new ApiResponse<>(200, false, "Email exist", null);
        }

        ApiResponse<User> response = userRepository.findByEmailIncludeDeleted(registerUser.getEmail())
                .map(user -> {
                    if (user.isDeleted()) {
                        log.debug("Restoring soft-deleted user account for email={}", registerUser.getEmail());
                        user.setDeleted(false);
                        user.setDeletedAt(null);
                        user.setFullName(registerUser.getFullName());
                        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
                        user.setNumberPhone(registerUser.getNumberPhone());
                        userRepository.save(user);
                        return new ApiResponse<>(200, true, "Login successfully", user);
                    } else {
                        return new ApiResponse<User>(200, false, "Email exist", null);
                    }
                })
                .orElseGet(() -> {
                    User newUser = createUser(registerUser);
                    if(!isUserActiveByEmail(registerUser.getEmail())) {
                        return new ApiResponse<>(200,true,"Create Account Successfully, please check email",newUser);
                    }
                    return new ApiResponse<>(200, false, "Your account is not active",newUser);
                });
        log.info("Signup process finished for email={} with success={}", registerUser.getEmail(), response.isSuccess());
        return response;
    }

    @Override
    @Transactional
    public ApiResponse<LoginResponse> authenticate(LoginUserRequest loginUser) {
        log.info("Starting authentication for email={}", loginUser.getEmail());
        User user =  userValidationService.validateAndGetUserByEmail(loginUser.getEmail());
        validateUserIsActive(user);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setRefreshToken(historyLoginService.createRefreshToken(userDetails.getUserId()).getData());

        log.info("Authentication successful for email={}", loginUser.getEmail());
        return new ApiResponse<>(200, true, "Login Successfully", loginResponse);
    }

    @Override
    @Transactional
    public ApiResponse<Void> logout(LogoutRequest request, String accessTokenHeader) {
        log.info("Starting logout process");
        try {
            blacklistAccessTokenIfPresent(accessTokenHeader);
            revokeRefreshTokenIfPresent(request);
            SecurityContextHolder.clearContext();
            log.info("Logout process finished successfully");
            return new ApiResponse<>(200, true, "Logout successfully", null);
        } catch (Exception ex) {
            log.error("Error during logout process", ex);
            throw ex;
        }
    }

    @Transactional
    @Override
    public ApiResponse<User> forgotPassword(ResetPasswordUserRequest request) {
        log.info("Starting forgot-password flow for email={}", request.getEmail());
        User user = userValidationService.validateAndGetUserByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("Forgot-password flow completed for email={}", request.getEmail());
        return new ApiResponse<>(200, true, "ResetPassword successfully", user);
    }

    @Override
    @Transactional
    public User createUser(RegisterUserRequest request) {
        return createUser(request, Role.ROLE_USER);
    }

    @Override
    @Transactional
    public User createUser(RegisterUserRequest request, Role role) {
        log.info("Creating user account for email={} with role={}", request.getEmail(), role);
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .numberPhone(request.getNumberPhone())
                .active(false)
                .build();
        String otp = String.valueOf(generateOtp());
        VerifyOTP verifyOTP = VerifyOTP.builder()
                .otp(otp)
                .user(user)
                .expertTime(LocalDateTime.now().plusMinutes(5))
                .build();
        emaiService.sendOtpEmail(user.getEmail(), otp);
        userRepository.save(user);
        verifyOTPRepository.save(verifyOTP);
        log.info("User account created for email={}", request.getEmail());
        return user;
    }

    private void validateUserIsActive(User user) {
        if(!user.getActive()) {
            log.debug("Authentication blocked because account is inactive: email={}", user.getEmail());
            throw new AppException("Please active your account");
        }
    }

    private void blacklistAccessTokenIfPresent(String accessTokenHeader) {
        if (StringUtils.hasText(accessTokenHeader) && accessTokenHeader.startsWith("Bearer ")) {
            String accessToken = accessTokenHeader.substring(7);
            Date expirationDate = jwtService.extractExpiration(accessToken);
            long timeToLive = expirationDate.getTime() - System.currentTimeMillis();
            if (timeToLive > 0) {
                redisService.saveToBlacklist(accessToken, timeToLive);
            }
        }
    }

    private void revokeRefreshTokenIfPresent(LogoutRequest request) {
        if (request.getRefreshToken() != null) {
            historyLoginService.revokeRefreshToken(request.getRefreshToken());
        }
    }



    private int generateOtp() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    private boolean isUserActiveByEmail(String email){
        return userRepository.findByEmail(email)
                .map(User::getActive)
                .orElse(false);
    }
}
