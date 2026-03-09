package com.example.SpringSecurity.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String key, int limit, int timeWindowSeconds) {
        long currentCount = Optional.ofNullable(redisTemplate.opsForValue().increment(key))
                .orElse(0L);

        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(timeWindowSeconds));
        }
        else if (redisTemplate.getExpire(key) == -1) {
            redisTemplate.expire(key, Duration.ofSeconds(timeWindowSeconds)); //  kiểm tra khi count > 1 để tối ưu hiệu năng
        }

        return currentCount <= limit;
    }
}
