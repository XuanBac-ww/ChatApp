package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.request.auth.LogoutRequest;
import com.example.SpringSecurity.dto.request.auth.ResetPasswordUserRequest;
import com.example.SpringSecurity.dto.request.otp.VerifyOtpRequest;
import com.example.SpringSecurity.dto.response.auth.LoginResponse;
import com.example.SpringSecurity.dto.request.auth.LoginUserRequest;
import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.service.historyLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.auth.IAuthService;
import com.example.SpringSecurity.service.verifyOTP.IVerifyOTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IHistoryLoginService historyLoginService;
    private final IVerifyOTPService verifyOTPService;


    @PostMapping("/signup")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<User> register(@RequestBody @Valid RegisterUserRequest registerUser) {
        return authService.signup(registerUser);
    }

    @RateLimit(limit = 5,timeWindowSeconds = 60)
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginUserRequest loginUser) {
        return authService.authenticate(loginUser);
    }

    @PostMapping("/refresh")
    public ApiResponse<String> refreshToken(@RequestBody Map<String,String> payload) {
        String refreshToken = payload.get("refreshToken");
        return historyLoginService.handleRefreshToken(refreshToken);
    }

    @PostMapping("/verify-otp")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        return verifyOTPService.verifyOTP(verifyOtpRequest);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request,
                                    @RequestHeader("Authorization") String authorizationHeader) {
        return authService.logout(request, authorizationHeader);
    }


    @PostMapping("/reset-password")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<User> resetPassword(@Valid @RequestBody ResetPasswordUserRequest request) {
        return authService.forgotPassword(request);
    }
}
