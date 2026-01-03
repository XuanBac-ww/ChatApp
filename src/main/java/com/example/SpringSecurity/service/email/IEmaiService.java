package com.example.SpringSecurity.service.email;

public interface IEmaiService {
    void sendOtpEmail(String to, String otp);
}
