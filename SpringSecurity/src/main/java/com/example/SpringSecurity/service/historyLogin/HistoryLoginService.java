package com.example.SpringSecurity.service.historyLogin;

import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.HistoryLogin;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IHistoryLoginRepository;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.service.IJwtService;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryLoginService implements IHistoryLoginService {

    @Value("${security.jwt.refreshExpirationMs}")
    private Long refreshExpiration;

    private final IJwtService jwtService;
    private final IHistoryLoginRepository historyLoginRepository;
    private final IUserValidationService userValidationService;

    @Transactional
    @Override
    public ApiResponse<String> createRefreshToken(Long userId) {
        log.info("Creating refresh token for userId={}", userId);

        if(userId == null) {
            return new ApiResponse<>(200,false,"Id is null",null);
        }

        HistoryLogin token = historyLoginRepository.findByUserId(userId)
                .orElseGet(HistoryLogin::new);

        User user = userValidationService.findById(userId);
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        token.setToken(UUID.randomUUID().toString());
        historyLoginRepository.save(token);
        log.info("Refresh token created successfully for userId={}", userId);
        return new ApiResponse<>(200,true,"RefreshToken Successfully",token.getToken());
    }



    @Override
    public ApiResponse<String> handleRefreshToken(String token) {
        log.info("Handling refresh token request");

        if(token == null || token.trim().isEmpty()) {
            return new ApiResponse<>(200, false, "Refresh token is required", null);
        }

        Optional<HistoryLogin> historyLogin = historyLoginRepository.findByToken(token);
        if (historyLogin.isEmpty()) {
            return new ApiResponse<>(200, false, "Token not found", null);
        }
        HistoryLogin refreshToken = historyLogin.get();
        if(isTokenExpired(refreshToken)) {
            historyLoginRepository.delete(refreshToken);
            log.debug("Refresh token expired and removed");
            return new ApiResponse<>(200, false, "Token expired", null);
        }
        log.info("Refresh token validated successfully");
        return new ApiResponse<>(200,true,"RefreshToken Successfully",jwtService.generateToken((UserDetails) refreshToken.getUser()));
    }

    @Transactional
    @Override
    public void revokeRefreshToken(String token) {
        log.info("Revoking refresh token");
        var storedToken = historyLoginRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Refresh token not found when revoking");
                    return new AppException("Token not found");
                });

        historyLoginRepository.delete(storedToken);
        log.info("Refresh token revoked successfully");
    }


    // helper
    private boolean isTokenExpired(HistoryLogin token) {
        if(token == null) {
            return true;
        }
        return token.getExpiryDate().isBefore(Instant.now());
    }

}
