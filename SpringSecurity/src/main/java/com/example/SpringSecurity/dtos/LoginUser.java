package com.example.SpringSecurity.dtos;

import lombok.Data;

@Data
public class LoginUser {
    private String email;
    private String password;
}
