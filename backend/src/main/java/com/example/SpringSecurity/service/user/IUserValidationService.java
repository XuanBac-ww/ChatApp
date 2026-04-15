package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.model.User;

public interface IUserValidationService {
    boolean findByEmail(String email);

    User validateAndGetUserByEmail(String email);

    User findById(Long userId);
}
