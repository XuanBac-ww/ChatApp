package com.example.SpringSecurity.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserRequest {
    @Email(message = "Email không đúng định dạng")
    private String email;
    @NotBlank(message = "password không được để trống")
    private String password;
}
