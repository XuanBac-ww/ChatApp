package com.example.SpringSecurity.service.HistoryLogin;

import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.HistoryLogin;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IHistoryLoginRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryLoginService implements IHistoryLoginService{

    @Value("${security.jwt.refreshExpirationMs}")
    private Long refreshExpiration;

    private final JwtService jwtService;
    private final IHistoryLoginRepository historyLoginRepository;
    private final IUserRepository userRepository;

    @Transactional
    @Override
    public ApiResponse<String> createRefreshToken(Long userId) {

        if(userId == null) {
            return new ApiResponse<>(400,false,"Id is null",null);
        }

        HistoryLogin token = historyLoginRepository.findByUserId(userId)
                .orElseGet(HistoryLogin::new);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found with id: " + userId));
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        token.setToken(UUID.randomUUID().toString());
        historyLoginRepository.save(token);
        return new ApiResponse<>(200,true,"RefreshToken Successfully",token.getToken());
    }

    public boolean isTokenExpired(HistoryLogin token) {
        if(token == null) {
            return true;
        }
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Override
    public ApiResponse<String> handleRefreshToken(String token) {

        if(token == null || token.trim().isEmpty()) {
            return new ApiResponse<>(400, false, "Refresh token is required", null);
        }

        Optional<HistoryLogin> historyLogin = historyLoginRepository.findByToken(token);
        if (historyLogin.isEmpty()) {
            return new ApiResponse<>(404, false, "Token not found", null);
        }
        HistoryLogin refreshToken = historyLogin.get();
        if(isTokenExpired(refreshToken)) {
            historyLoginRepository.delete(refreshToken);
            return new ApiResponse<>(401, false, "Token expired", null);
        }
        return new ApiResponse<>(200,true,"RefreshToken Successfully",jwtService.generateToken((UserDetails) refreshToken.getUser()));
    }

}
