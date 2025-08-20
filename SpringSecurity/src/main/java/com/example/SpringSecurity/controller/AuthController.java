package com.example.SpringSecurity.controller;

import com.example.SpringSecurity.dtos.auth.LoginResponse;
import com.example.SpringSecurity.dtos.auth.LoginUserRequest;
import com.example.SpringSecurity.dtos.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.response.ApiResponse;
import com.example.SpringSecurity.service.HistoryLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.JwtService;
import com.example.SpringSecurity.service.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IHistoryLoginService historyLoginService;

    @PostMapping("/signup")
    public ApiResponse<User> register(@RequestBody RegisterUserRequest registerUser) {
        return authService.signup(registerUser);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginUserRequest loginUser) {
        return authService.authenticate(loginUser);
    }

    @PostMapping("/refresh")
    public ApiResponse<String> refreshToken(@RequestBody Map<String,String> payload) {
        String refreshToken = payload.get("refreshToken");
        return historyLoginService.handleRefreshToken(refreshToken);
    }
}
