package com.example.SpringSecurity.service.auth;

import com.example.SpringSecurity.dto.request.auth.LoginUserRequest;
import com.example.SpringSecurity.dto.request.auth.LogoutRequest;
import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.auth.LoginResponse;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.repository.IVerifyOTPRepository;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.IJwtService;
import com.example.SpringSecurity.service.IRedisService;
import com.example.SpringSecurity.service.email.IEmaiService;
import com.example.SpringSecurity.service.historyLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.user.IUserValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private IJwtService jwtService;

    @Mock
    private IHistoryLoginService historyLoginService;

    @Mock
    private IVerifyOTPRepository verifyOTPRepository;

    @Mock
    private IEmaiService emaiService;

    @Mock
    private IUserValidationService userValidationService;

    @Mock
    private IRedisService redisService;

    @InjectMocks
    private AuthService authService;

    @Test
    void signup_shouldReturnFailure_whenEmailAlreadyExists() {
        RegisterUserRequest request = buildRegisterRequest();

        when(userValidationService.findByEmail(request.getEmail())).thenReturn(true);

        ApiResponse<User> response = authService.signup(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Email exist");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_shouldThrowException_whenUserIsInactive() {
        LoginUserRequest request = buildLoginRequest();
        User user = buildUser(false);

        when(userValidationService.validateAndGetUserByEmail(request.getEmail())).thenReturn(user);

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(AppException.class)
                .hasMessage("Please active your account");

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void authenticate_shouldReturnLoginResponse_whenCredentialsAreValid() {
        LoginUserRequest request = buildLoginRequest();
        User user = buildUser(true);
        CustomUserDetails userDetails = new CustomUserDetails(
                15L,
                "alice@example.com",
                "Alice",
                "encoded-password",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(userValidationService.validateAndGetUserByEmail(request.getEmail())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");
        when(jwtService.getExpirationTime()).thenReturn(900000L);
        when(historyLoginService.createRefreshToken(15L))
                .thenReturn(new ApiResponse<>(200, true, "ok", "refresh-token"));

        ApiResponse<LoginResponse> response = authService.authenticate(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getToken()).isEqualTo("access-token");
        assertThat(response.getData().getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getData().getExpiresIn()).isEqualTo(900000L);
    }

    @Test
    void logout_shouldBlacklistAccessTokenAndRevokeRefreshToken_whenTokensArePresent() {
        LogoutRequest request = new LogoutRequest();
        request.setRefreshToken("refresh-token");

        when(jwtService.extractExpiration("access-token"))
                .thenReturn(new Date(System.currentTimeMillis() + 60_000));

        ApiResponse<Void> response = authService.logout(request, "Bearer access-token");

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);

        assertThat(response.isSuccess()).isTrue();
        verify(redisService).saveToBlacklist(eq("access-token"), ttlCaptor.capture());
        verify(historyLoginService).revokeRefreshToken("refresh-token");
        assertThat(ttlCaptor.getValue()).isPositive();
    }

    @Test
    void createUser_shouldSaveUserAndOtpAndSendEmail() {
        RegisterUserRequest request = buildRegisterRequest();
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        User createdUser = authService.createUser(request, Role.ROLE_USER);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<VerifyOTP> verifyOtpCaptor = ArgumentCaptor.forClass(VerifyOTP.class);
        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);

        verify(userRepository).save(userCaptor.capture());
        verify(verifyOTPRepository).save(verifyOtpCaptor.capture());
        verify(emaiService).sendOtpEmail(eq("alice@example.com"), otpCaptor.capture());

        User savedUser = userCaptor.getValue();
        VerifyOTP savedVerifyOtp = verifyOtpCaptor.getValue();

        assertThat(createdUser).isSameAs(savedUser);
        assertThat(savedUser.getPassword()).isEqualTo("encoded-secret");
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(savedUser.getActive()).isFalse();
        assertThat(otpCaptor.getValue()).matches("\\d{6}");
        assertThat(savedVerifyOtp.getOtp()).isEqualTo(otpCaptor.getValue());
        assertThat(savedVerifyOtp.getUser()).isSameAs(savedUser);
        assertThat(savedVerifyOtp.getExpertTime()).isAfter(LocalDateTime.now().plusMinutes(4));
    }

    private RegisterUserRequest buildRegisterRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("alice@example.com");
        request.setFullName("Alice");
        request.setPassword("secret");
        request.setNumberPhone("0123456789");
        return request;
    }

    private LoginUserRequest buildLoginRequest() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("alice@example.com");
        request.setPassword("secret");
        return request;
    }

    private User buildUser(boolean active) {
        User user = User.builder()
                .fullName("Alice")
                .email("alice@example.com")
                .password("encoded-password")
                .numberPhone("0123456789")
                .role(Role.ROLE_USER)
                .active(active)
                .build();
        user.setId(15L);
        return user;
    }
}
