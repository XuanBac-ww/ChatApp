package com.example.SpringSecurity.service.verifyOTP;

import com.example.SpringSecurity.dto.request.otp.VerifyOtpRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.repository.IVerifyOTPRepository;
import com.example.SpringSecurity.service.user.IUserValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyOTPServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IVerifyOTPRepository verifyOTPRepository;

    @Mock
    private IUserValidationService userValidationService;

    @InjectMocks
    private VerifyOTPService verifyOTPService;

    @Test
    void verifyOTP_shouldReturnFailure_whenOtpDoesNotMatch() {
        User user = buildUser();
        VerifyOtpRequest request = buildRequest("alice@example.com", "111111");
        VerifyOTP verifyOTP = buildVerifyOtp(user, "222222", LocalDateTime.now().plusMinutes(5));

        when(userValidationService.validateAndGetUserByEmail("alice@example.com")).thenReturn(user);
        when(verifyOTPRepository.findByUser(user)).thenReturn(Optional.of(verifyOTP));

        ApiResponse<?> response = verifyOTPService.verifyOTP(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("OTP khong chinh xac");
        verify(userRepository, never()).save(user);
        verify(verifyOTPRepository, never()).delete(verifyOTP);
    }

    @Test
    void verifyOTP_shouldReturnFailure_whenOtpIsExpired() {
        User user = buildUser();
        VerifyOtpRequest request = buildRequest("alice@example.com", "111111");
        VerifyOTP verifyOTP = buildVerifyOtp(user, "111111", LocalDateTime.now().minusMinutes(1));

        when(userValidationService.validateAndGetUserByEmail("alice@example.com")).thenReturn(user);
        when(verifyOTPRepository.findByUser(user)).thenReturn(Optional.of(verifyOTP));

        ApiResponse<?> response = verifyOTPService.verifyOTP(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("OTP da qua han");
        verify(userRepository, never()).save(user);
        verify(verifyOTPRepository, never()).delete(verifyOTP);
    }

    @Test
    void verifyOTP_shouldActivateUserAndDeleteOtp_whenOtpIsValid() {
        User user = buildUser();
        user.setActive(false);
        VerifyOtpRequest request = buildRequest("alice@example.com", "111111");
        VerifyOTP verifyOTP = buildVerifyOtp(user, "111111", LocalDateTime.now().plusMinutes(5));

        when(userValidationService.validateAndGetUserByEmail("alice@example.com")).thenReturn(user);
        when(verifyOTPRepository.findByUser(user)).thenReturn(Optional.of(verifyOTP));

        ApiResponse<?> response = verifyOTPService.verifyOTP(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Xac thuc thanh cong");
        assertThat(user.getActive()).isTrue();
        verify(userRepository).save(user);
        verify(verifyOTPRepository).delete(verifyOTP);
    }

    @Test
    void verifyOTP_shouldThrowException_whenOtpRecordDoesNotExist() {
        User user = buildUser();
        VerifyOtpRequest request = buildRequest("alice@example.com", "111111");

        when(userValidationService.validateAndGetUserByEmail("alice@example.com")).thenReturn(user);
        when(verifyOTPRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifyOTPService.verifyOTP(request))
                .isInstanceOf(AppException.class);
    }

    private User buildUser() {
        return User.builder()
                .fullName("Alice")
                .email("alice@example.com")
                .password("secret")
                .numberPhone("0123456789")
                .build();
    }

    private VerifyOtpRequest buildRequest(String email, String otp) {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail(email);
        request.setOtp(otp);
        return request;
    }

    private VerifyOTP buildVerifyOtp(User user, String otp, LocalDateTime expertTime) {
        return VerifyOTP.builder()
                .otp(otp)
                .user(user)
                .expertTime(expertTime)
                .build();
    }
}
