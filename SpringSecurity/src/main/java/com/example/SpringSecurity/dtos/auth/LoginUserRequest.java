package com.example.SpringSecurity.dtos.auth;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String email;
    private String password;
}
