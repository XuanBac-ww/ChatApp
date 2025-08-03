package com.example.SpringSecurity.service;

import com.example.SpringSecurity.exception.ResourceGoneException;
import com.example.SpringSecurity.exception.ResourceNotFoundException;
import com.example.SpringSecurity.model.RefreshToken;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.RefreshTokenRepository;
import com.example.SpringSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${security.jwt.refreshExpirationMs}")
    private Long refreshExpiration;

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createRefreshToken(Long userId) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElseGet(RefreshToken::new);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        token.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public String handleRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->  new ResourceNotFoundException("Token Not Found"));

        if(isTokenExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResourceGoneException("Refresh Token expired. Please Login again");
        }
        return jwtService.generateToken(refreshToken.getUser());
    }

}
