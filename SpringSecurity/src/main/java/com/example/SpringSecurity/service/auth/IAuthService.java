package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dtos.auth.LoginResponse;
import com.example.SpringSecurity.dtos.auth.LoginUserRequest;
import com.example.SpringSecurity.dtos.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.response.ApiResponse;

public interface IAuthService {
    ApiResponse<User> signup(RegisterUserRequest registerUser);

    ApiResponse<LoginResponse> authenticate(LoginUserRequest loginUser);
}
