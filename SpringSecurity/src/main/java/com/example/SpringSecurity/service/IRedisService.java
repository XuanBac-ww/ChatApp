package com.example.SpringSecurity.service;

public interface IRedisService {
    void saveToBlacklist(String token, long timeToLiveInMillis);

    boolean isBlacklisted(String token);
}
