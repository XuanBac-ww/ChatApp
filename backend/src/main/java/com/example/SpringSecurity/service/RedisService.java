package com.example.SpringSecurity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService implements IRedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveToBlacklist(String token, long timeToLiveInMillis) {
        log.info("Saving token to blacklist with TTL={}ms", timeToLiveInMillis);
        redisTemplate.opsForValue().set(token, "blacklisted", timeToLiveInMillis, TimeUnit.MILLISECONDS);
        log.info("Token saved to blacklist successfully");
    }

    @Override
    public boolean isBlacklisted(String token) {
        log.debug("Checking whether token exists in blacklist");
        Boolean hasKey = redisTemplate.hasKey(token);
        return Boolean.TRUE.equals(hasKey);
    }

}
