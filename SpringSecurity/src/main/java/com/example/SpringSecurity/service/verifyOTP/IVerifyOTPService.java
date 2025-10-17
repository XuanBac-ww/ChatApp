package com.example.SpringSecurity.service.verifyOTP;

import com.example.SpringSecurity.dto.request.otp.VerifyOtpRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.otp.VerifyOtpResponse;

public interface IVerifyOTPService {
    ApiResponse<VerifyOtpResponse> verifyOTP(VerifyOtpRequest verifyOtpRequest);
}
