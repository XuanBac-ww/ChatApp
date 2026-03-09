package com.example.SpringSecurity.service.historyLogin;

import com.example.SpringSecurity.dto.response.api.ApiResponse;

public interface IHistoryLoginService {
    ApiResponse<String> createRefreshToken(Long userId);

    ApiResponse<String> handleRefreshToken(String token);

    void revokeRefreshToken(String token);
}
