package com.example.SpringSecurity.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @Email
    private String email;
    @NotBlank
    private String fullName;
    @NotBlank
    private String password;
    @NotBlank
    @Pattern(regexp = "^0\\d{9}$")
    @Size(max = 10,message = "So dien thoai toi da la 10 chu so va khong duoc de trong")
    private String numberPhone;
}
