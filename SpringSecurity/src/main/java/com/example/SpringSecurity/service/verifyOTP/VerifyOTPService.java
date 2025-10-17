package com.example.SpringSecurity.service.verifyOTP;

import com.example.SpringSecurity.dto.request.otp.VerifyOtpRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.otp.VerifyOtpResponse;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.repository.IVerifyOTPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifyOTPService implements IVerifyOTPService{
    private final IUserRepository userRepository;
    private final IVerifyOTPRepository verifyOTPRepository;
    @Override
    public ApiResponse<VerifyOtpResponse> verifyOTP(VerifyOtpRequest verifyOtpRequest) {
        User user = userRepository.findByEmail(verifyOtpRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        VerifyOTP verifyOTP = verifyOTPRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã OTP cho người dùng này"));
        if (!verifyOTP.getOtp().equals(verifyOtpRequest.getOtp())) {
            return new ApiResponse<>(200,true,"OTP khong chinh xac",null);
        }
        if (verifyOTP.getExpertTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(200,true,"OTP da qua han",null);
        }
        user.setActive(true);
        userRepository.save(user);
        verifyOTPRepository.delete(verifyOTP);
        return new ApiResponse<>(200,true,"Xac thuc thanh cong",null);
    }
}
