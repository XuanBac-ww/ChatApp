package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dto.request.auth.LogoutRequest;
import com.example.SpringSecurity.dto.response.auth.LoginResponse;
import com.example.SpringSecurity.dto.request.auth.LoginUserRequest;
import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.dto.response.api.ApiResponse;

public interface IAuthService {
    ApiResponse<User> signup(RegisterUserRequest registerUser);

    ApiResponse<LoginResponse> authenticate(LoginUserRequest loginUser);

    ApiResponse<Void> logout(LogoutRequest request, String accessToken);

    User createUser(RegisterUserRequest request);

    User createUser(RegisterUserRequest request, Role role);
}
