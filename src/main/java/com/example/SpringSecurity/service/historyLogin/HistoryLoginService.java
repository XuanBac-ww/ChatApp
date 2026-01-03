package com.example.SpringSecurity.service.historyLogin;

import com.example.SpringSecurity.model.HistoryLogin;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IHistoryLoginRepository;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.service.JwtService;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryLoginService implements IHistoryLoginService{

    @Value("${security.jwt.refreshExpirationMs}")
    private Long refreshExpiration;

    private final JwtService jwtService;
    private final IHistoryLoginRepository historyLoginRepository;
    private final IUserValidationService userValidationService;

    @Transactional
    @Override
    public ApiResponse<String> createRefreshToken(Long userId) {

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
        return new ApiResponse<>(200,true,"RefreshToken Successfully",token.getToken());
    }



    @Override
    public ApiResponse<String> handleRefreshToken(String token) {

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
            return new ApiResponse<>(200, false, "Token expired", null);
        }
        return new ApiResponse<>(200,true,"RefreshToken Successfully",jwtService.generateToken((UserDetails) refreshToken.getUser()));
    }

    @Override
    public void revokeRefreshToken(String token) {
        var storedToken = historyLoginRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        historyLoginRepository.delete(storedToken);
    }


    // helper
    private boolean isTokenExpired(HistoryLogin token) {
        if(token == null) {
            return true;
        }
        return token.getExpiryDate().isBefore(Instant.now());
    }

}
