package com.example.SpringSecurity.service.HistoryLogin;

import org.springframework.transaction.annotation.Transactional;

public interface IHistoryLoginService {
    @Transactional
    String createRefreshToken(Long userId);

    String handleRefreshToken(String token);
}
