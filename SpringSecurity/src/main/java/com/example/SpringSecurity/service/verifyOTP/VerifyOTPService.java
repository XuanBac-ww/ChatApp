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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerifyOTPService implements IVerifyOTPService{

    private final IUserRepository userRepository;
    private final IVerifyOTPRepository verifyOTPRepository;
    private final IUserValidationService userValidationService;

    @Override
    @Transactional
    public ApiResponse<?> verifyOTP(VerifyOtpRequest verifyOtpRequest) {
        log.info("Starting OTP verification for email={}", verifyOtpRequest.getEmail());
        User user = userValidationService.validateAndGetUserByEmail(verifyOtpRequest.getEmail());
        VerifyOTP verifyOTP = verifyOTPRepository.findByUser(user)
                .orElseThrow(() -> new AppException("Không tìm thấy mã OTP cho người dùng này"));
        if (!isOtpMatched(verifyOTP, verifyOtpRequest)) {
            log.debug("OTP verification failed because OTP does not match for email={}", verifyOtpRequest.getEmail());
            return new ApiResponse<>(200,false,"OTP khong chinh xac",null);
        }
        if (isOtpExpired(verifyOTP)) {
            log.debug("OTP verification failed because OTP expired for email={}", verifyOtpRequest.getEmail());
            return new ApiResponse<>(200,false,"OTP da qua han",null);
        }
        user.setActive(true);
        userRepository.save(user);
        verifyOTPRepository.delete(verifyOTP);
        log.info("OTP verification success for email={}", verifyOtpRequest.getEmail());
        return new ApiResponse<>(200,true,"Xac thuc thanh cong",null);
    }

    private boolean isOtpMatched(VerifyOTP verifyOTP, VerifyOtpRequest request) {
        return verifyOTP.getOtp().equals(request.getOtp());
    }

    private boolean isOtpExpired(VerifyOTP verifyOTP) {
        return verifyOTP.getExpertTime().isBefore(LocalDateTime.now());
    }
}
