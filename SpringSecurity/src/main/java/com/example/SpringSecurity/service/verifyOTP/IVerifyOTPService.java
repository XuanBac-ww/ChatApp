package com.example.SpringSecurity.service.verifyOTP;

import com.example.SpringSecurity.dto.request.otp.VerifyOtpRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;

public interface IVerifyOTPService {
    ApiResponse<?> verifyOTP(VerifyOtpRequest verifyOtpRequest);
}
