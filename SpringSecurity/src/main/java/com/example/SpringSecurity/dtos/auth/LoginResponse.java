package com.example.SpringSecurity.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private Long expiresIn;
    private String refreshToken;
}
