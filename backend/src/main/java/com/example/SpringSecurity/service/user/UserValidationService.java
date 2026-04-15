package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidationService implements IUserValidationService{

    private final IUserRepository userRepository;

    @Override
    public boolean findByEmail(String email) {
        log.debug("Checking if user exists by email={}", email);
        return userRepository.findByEmail(email)
                .isPresent();
    }

    @Override
    public User validateAndGetUserByEmail(String email) {
        log.debug("Validating user by email={}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User Not Found"));
    }


    @Override
    public User findById(Long userId) {
        log.debug("Validating user by id={}", userId);
        return  userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
    }
}
