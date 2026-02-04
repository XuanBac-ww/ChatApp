package com.example.SpringSecurity.service.verifyOTP;

import com.example.SpringSecurity.dto.request.otp.VerifyOtpRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.repository.IVerifyOTPRepository;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifyOTPService implements IVerifyOTPService{

    private final IUserRepository userRepository;
    private final IVerifyOTPRepository verifyOTPRepository;
    private final IUserValidationService userValidationService;

    @Override
    public ApiResponse<?> verifyOTP(VerifyOtpRequest verifyOtpRequest) {
        User user = userValidationService.validateAndGetUserByEmail(verifyOtpRequest.getEmail());
        VerifyOTP verifyOTP = verifyOTPRepository.findByUser(user)
                .orElseThrow(() -> new AppException("Không tìm thấy mã OTP cho người dùng này"));
        if (!verifyOTP.getOtp().equals(verifyOtpRequest.getOtp())) {
            return new ApiResponse<>(200,false,"OTP khong chinh xac",null);
        }
        if (verifyOTP.getExpertTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(200,false,"OTP da qua han",null);
        }
        user.setActive(true);
        userRepository.save(user);
        verifyOTPRepository.delete(verifyOTP);
        return new ApiResponse<>(200,true,"Xac thuc thanh cong",null);
    }
}
