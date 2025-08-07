package com.example.SpringSecurity.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserRequest {

    @Email
    private String email;

    @NotBlank
    private String password;
}
