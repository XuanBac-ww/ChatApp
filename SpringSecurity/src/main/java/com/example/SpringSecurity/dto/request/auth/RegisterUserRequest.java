package com.example.SpringSecurity.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @Email
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String password;
}
