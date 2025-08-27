package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dto.response.auth.LoginResponse;
import com.example.SpringSecurity.dto.request.auth.LoginUserRequest;
import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.HistoryLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.JwtService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements IAuthService{
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IHistoryLoginService historyLoginService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ApiResponse<User> signup(RegisterUserRequest registerUser) {
        logger.info("Start signup with email: {}", registerUser.getEmail());


        return userRepository.findByEmailIncludeDeleted(registerUser.getEmail())
                .map(user -> {
                    if (user.isDeleted()) {
                        userRepository.restore(user.getId());
                        entityManager.refresh(user);
                        user.setFullName(registerUser.getFullName());
                        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
                        return new ApiResponse<>(200, true, "Sign Up Successfully", user);
                    } else {
                        return new ApiResponse<User>(400, false, "Email already exists", null);
                    }
                })
                .orElseGet(() -> {
                    User newUser = createUser(registerUser);
                    return new ApiResponse<>(200, true, "Sign Up Successfully",newUser);
                });
    }
    @Override
    public ApiResponse<LoginResponse> authenticate(LoginUserRequest loginUser) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );
        User user =  userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new AppException("User Not Found"));

        CustomUserDetails userDetails = CustomUserDetails.fromUserEntity(user);
        String jwt = jwtService.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setRefreshToken(historyLoginService.createRefreshToken(user.getId()).getData());

        return new ApiResponse<>(200,true,"Sign In Successfully",loginResponse);
    }


    @Override
    public User createUser(RegisterUserRequest request) {
        return createUser(request, Role.ROLE_USER);
    }

    @Override
    public User createUser(RegisterUserRequest request, Role role) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        return userRepository.save(user);
    }
}
