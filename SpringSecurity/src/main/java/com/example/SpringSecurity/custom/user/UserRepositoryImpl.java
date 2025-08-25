package com.example.SpringSecurity.custom.user;

import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepositoryCustom {

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    @Override
    public User createUser(RegisterUserRequest userRequest) {
        User user = User.builder()
                .fullName(userRequest.getFullName())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        return userRepository.save(user);
    }
}
