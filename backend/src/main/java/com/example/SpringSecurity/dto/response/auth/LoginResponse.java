package com.example.SpringSecurity.dto.response.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private Long expiresIn;
    private String refreshToken;
}
