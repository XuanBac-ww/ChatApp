package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dtos.auth.LoginResponse;
import com.example.SpringSecurity.dtos.auth.LoginUserRequest;
import com.example.SpringSecurity.dtos.auth.RegisterUserRequest;
import com.example.SpringSecurity.exception.ResourceNotFoundException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.UserRepository;
import com.example.SpringSecurity.response.ApiResponse;
import com.example.SpringSecurity.service.HistoryLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;



@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements IAuthService{
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IHistoryLoginService historyLoginService;

    @Override
    public ApiResponse<User> signup(RegisterUserRequest registerUser) {
        logger.info("Start signup with email: {}", registerUser.getEmail());
        if(!StringUtils.hasText(registerUser.getFullName())) {
            log.info("Signup validation failed: fullName is empty, email={}", registerUser.getEmail());
            return new ApiResponse<>(400,false,"FullName is required",null);
        }

        if (!StringUtils.hasText(registerUser.getEmail())) {
            log.info("Signup validation failed: Email is empty, fullName={}", registerUser.getFullName());

            return new ApiResponse<>(400,false,"Email is required",null);
        }

        if (!StringUtils.hasText(registerUser.getPassword())) {
            log.info("Signup validation failed: Password is empty, email={}", registerUser.getEmail());

            return new ApiResponse<>(400,false,"Password is required",null);
        }

        return userRepository.findByEmail(registerUser.getEmail())
                .map(u -> new ApiResponse<User>(400,false,"Email exit",null))
                .orElseGet(() -> {
                    User  user = User.builder()
                            .fullName(registerUser.getFullName())
                            .email(registerUser.getEmail())
                            .password(passwordEncoder.encode(registerUser.getPassword()))
                            .build();
                    userRepository.save(user);
                    logger.info("User created successfully with email: {}", user.getEmail());
                    return new ApiResponse<>(200,true,"Sign Up Successfully",user);
                });
    }
    @Override
    public ApiResponse<LoginResponse> authenticate(LoginUserRequest loginUser) {
        if(!StringUtils.hasText(loginUser.getEmail())) {
            return new ApiResponse<>(400,false,"Email is required",null);
        }
        if(!StringUtils.hasText(loginUser.getPassword())) {
            return new ApiResponse<>(400,false,"Password is required",null);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );
        User user =  userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        String jwt = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setRefreshToken(historyLoginService.createRefreshToken(user.getId()).getData());
        return new ApiResponse<>(200,true,"Sign Up Successfully",loginResponse);
    }
}
