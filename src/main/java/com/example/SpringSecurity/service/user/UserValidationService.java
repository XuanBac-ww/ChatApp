package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationService implements IUserValidationService{

    private final IUserRepository userRepository;

    @Override
    public boolean findByEmail(String email) {
        return userRepository.findByEmail(email)
                .isPresent();
    }

    @Override
    public User validateAndGetUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User Not Found"));
    }


    @Override
    public User findById(Long userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
    }
}
