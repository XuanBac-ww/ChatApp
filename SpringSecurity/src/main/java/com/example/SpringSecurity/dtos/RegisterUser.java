package com.example.SpringSecurity.dtos;

import lombok.Data;

@Data
public class RegisterUser {
    private String email;
    private String fullName;
    private String password;
}
