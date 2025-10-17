package com.example.SpringSecurity.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String key, int limit, int timeWindowSeconds) {
        Long currentCount = redisTemplate.opsForValue().increment(key);
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(timeWindowSeconds));
        }
        return currentCount <= limit;
    }

}
