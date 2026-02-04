package com.example.SpringSecurity.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmaiService{

    private final JavaMailSender javaMailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã xác Tài khoản của bạn");
        message.setText("Mã Otp của bạn là: " + otp + "\nMã Sẽ hết hạn sau 5 phút");
        javaMailSender.send(message);
    }
}
