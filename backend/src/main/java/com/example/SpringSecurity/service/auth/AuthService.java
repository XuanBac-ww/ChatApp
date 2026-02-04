package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dto.request.auth.LogoutRequest;
import com.example.SpringSecurity.dto.response.auth.LoginResponse;
import com.example.SpringSecurity.dto.request.auth.LoginUserRequest;
import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.repository.IVerifyOTPRepository;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.RedisService;
import com.example.SpringSecurity.service.email.IEmaiService;
import com.example.SpringSecurity.service.historyLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.JwtService;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService{

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IHistoryLoginService historyLoginService;
    private final IVerifyOTPRepository verifyOTPRepository;
    private final IEmaiService emaiService;
    private final IUserValidationService userValidationService;
    private final RedisService redisService;

    @Override
    @Transactional
    @CacheEvict(value = "users_list", allEntries = true)
    public ApiResponse<User> signup(RegisterUserRequest registerUser) {
        if(userValidationService.findByEmail(registerUser.getEmail())) {
            return new ApiResponse<>(200, false, "Email exist", null);
        }

        return userRepository.findByEmailIncludeDeleted(registerUser.getEmail())
                .map(user -> {
                    if (user.isDeleted()) {
                        user.setDeleted(false);
                        user.setDeletedAt(null);
                        user.setFullName(registerUser.getFullName());
                        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
                        return new ApiResponse<>(200, true, "Login successfully", user);
                    } else {
                        return new ApiResponse<User>(200, false, "Email exist", null);
                    }
                })
                .orElseGet(() -> {
                    User newUser = createUser(registerUser);
                    if(!IsUserActiveByEmail(registerUser.getEmail())) {
                        return new ApiResponse<>(200,true,"Create Account Successfully, please check email",newUser);
                    }
                    return new ApiResponse<>(200, false, "Your account is not active",newUser);
                });
    }

    @Override
    @Transactional
    public ApiResponse<LoginResponse> authenticate(LoginUserRequest loginUser) {
        User user =  userValidationService.validateAndGetUserByEmail(loginUser.getEmail());
        if(!user.getActive()) {
            return new ApiResponse<>(200,false,"Please active your account",null);
        }

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

        return new ApiResponse<>(200, true, "Login Successfully", loginResponse);
    }


    @Override
    @Transactional
    public ApiResponse<Void> logout(LogoutRequest request, String accessTokenHeader) {
        if (StringUtils.hasText(accessTokenHeader) && accessTokenHeader.startsWith("Bearer ")) {
            String accessToken = accessTokenHeader.substring(7);
            Date expirationDate = jwtService.extractExpiration(accessToken);
            long timeToLive = expirationDate.getTime() - System.currentTimeMillis();
            if (timeToLive > 0) {
                redisService.saveToBlacklist(accessToken, timeToLive);
            }
        }
        if (request.getRefreshToken() != null) {
            historyLoginService.revokeRefreshToken(request.getRefreshToken());
        }
        SecurityContextHolder.clearContext();
        return new ApiResponse<>(200, true, "Logout successfully", null);
    }


    @Override
    @Transactional
    public User createUser(RegisterUserRequest request) {
        return createUser(request, Role.ROLE_USER);
    }

    @Override
    @Transactional
    public User createUser(RegisterUserRequest request, Role role) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .numberPhone(request.getNumberPhone())
                .active(false)
                .build();
        String otp = String.valueOf(OtpRandom());
        VerifyOTP verifyOTP = VerifyOTP.builder()
                .otp(otp)
                .user(user)
                .expertTime(LocalDateTime.now().plusMinutes(5))
                .build();
        emaiService.sendOtpEmail(user.getEmail(), otp);
        userRepository.save(user);
        verifyOTPRepository.save(verifyOTP);
        return user;
    }



    private int OtpRandom() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    private boolean IsUserActiveByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            boolean isActive = user.get().getActive();
            if(!isActive)
                return false;
            return true;
        }
        return false;
    }
}
