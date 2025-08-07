package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dtos.auth.LoginUserRequest;
import com.example.SpringSecurity.dtos.auth.RegisterUserRequest;
import com.example.SpringSecurity.exception.ResourceNotFoundException;
import com.example.SpringSecurity.exception.ValidateException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements IAuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User signup(RegisterUserRequest registerUser) {
        log.info("Attempting to register user with email: {}", registerUser.getEmail());

        if(registerUser.getFullName() == null) {
            throw new ValidateException("FullName should be not Null");
        }

        if(registerUser.getEmail() == null) {
            throw new ValidateException("Email should be not Null");
        }
        if(registerUser.getPassword() == null) {
            throw new ValidateException("Password should be not Null");
        }
        User user = User.builder()
                .fullName(registerUser.getFullName())
                .email(registerUser.getEmail())
                .password(passwordEncoder.encode(registerUser.getPassword()))
                .build();

        log.info("User registered successfully with ID: {}", user.getId());
        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginUserRequest loginUser) {

        log.info("Attempting to authenticate user with email: {}", loginUser.getEmail());
        if(loginUser.getEmail() == null) {
            throw new ValidateException("Email should be not Null");
        }
        if(loginUser.getPassword() == null) {
            throw new ValidateException("Email should be not Null");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );


        User user =  userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        log.info("User authenticated successfully: {}", user.getId());
        return user;
    }

}
