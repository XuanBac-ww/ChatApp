package com.example.SpringSecurity.service.HistoryLogin;

import com.example.SpringSecurity.exception.ResourceGoneException;
import com.example.SpringSecurity.exception.ResourceNotFoundException;
import com.example.SpringSecurity.exception.ValidateException;
import com.example.SpringSecurity.model.HistoryLogin;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.HistoryLoginRepository;
import com.example.SpringSecurity.repository.UserRepository;
import com.example.SpringSecurity.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryLoginService implements IHistoryLoginService{

    @Value("${security.jwt.refreshExpirationMs}")
    private Long refreshExpiration;

    private final JwtService jwtService;
    private final HistoryLoginRepository historyLoginRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public String createRefreshToken(Long userId) {

        if(userId == null) {
            throw new ValidateException("userId is null");
        }

        log.info("Creating refresh token for user ID: {}", userId);
        HistoryLogin token = historyLoginRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.debug("No existing token found for user ID: {}, creating new one", userId);
                    return new HistoryLogin();
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found when creating refresh token: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        token.setToken(UUID.randomUUID().toString());
        historyLoginRepository.save(token);
        log.info("Refresh token created for user ID: {}, token: {}", userId, token.getToken());
        return token.getToken();
    }

    public boolean isTokenExpired(HistoryLogin token) {
        if(token == null) {
            throw new ValidateException("token is null");
        }
        boolean expired = token.getExpiryDate().isBefore(Instant.now());
        log.debug("Checking if token is expired (token: {}): {}", token.getToken(), expired);
        return expired;
    }

    @Override
    public String handleRefreshToken(String token) {

        if(token == null) {
            throw new ValidateException("token is null");
        }
        log.info("Handling refresh token request: {}", token);
        HistoryLogin refreshToken = historyLoginRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}", token);
                    return new ResourceNotFoundException("Token Not Found");
                });

        if(isTokenExpired(refreshToken)) {
            log.warn("Refresh token expired: {}", token);
            historyLoginRepository.delete(refreshToken);
            throw new ResourceGoneException("Refresh Token expired. Please Login again");
        }
        String jwt = jwtService.generateToken(refreshToken.getUser());
        log.info("Refresh token valid. New JWT issued for user ID: {}", refreshToken.getUser().getId());
        return jwt;
    }

}
