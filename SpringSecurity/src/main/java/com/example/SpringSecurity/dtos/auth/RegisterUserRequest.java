package com.example.SpringSecurity.dtos.auth;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String email;
    private String fullName;
    private String password;
}
