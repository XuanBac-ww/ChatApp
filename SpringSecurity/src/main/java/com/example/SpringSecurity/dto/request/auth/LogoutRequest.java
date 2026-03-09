package com.example.SpringSecurity.dto.request.auth;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}