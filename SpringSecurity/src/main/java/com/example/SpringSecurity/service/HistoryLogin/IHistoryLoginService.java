package com.example.SpringSecurity.service.HistoryLogin;

import com.example.SpringSecurity.response.ApiResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IHistoryLoginService {
    @Transactional
    ApiResponse<String> createRefreshToken(Long userId);

    ApiResponse<String> handleRefreshToken(String token);
}
