package com.example.SpringSecurity.service.email;

import com.example.SpringSecurity.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmaiService{

    private final JavaMailSender javaMailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        log.info("Sending OTP email to recipient={}", to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Mã xác Tài khoản của bạn");
            message.setText("Mã Otp của bạn là: " + otp + "\nMã Sẽ hết hạn sau 5 phút");
            javaMailSender.send(message);
            log.info("OTP email sent successfully to recipient={}", to);
        } catch (MailException ex) {
            log.error("Failed to send OTP email to recipient={}", to, ex);
            throw new AppException("Failed to send OTP email", ex);
        }
    }
}
