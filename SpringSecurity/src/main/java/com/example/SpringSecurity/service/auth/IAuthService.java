package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dtos.auth.LoginUserRequest;
import com.example.SpringSecurity.dtos.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;

public interface IAuthService {
    User signup(RegisterUserRequest registerUser);

    User authenticate(LoginUserRequest loginUser);
}
