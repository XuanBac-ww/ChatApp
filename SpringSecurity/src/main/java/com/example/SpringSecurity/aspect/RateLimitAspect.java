package com.example.SpringSecurity.aspect;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.exception.RateLimitExceedException;
import com.example.SpringSecurity.infrastructure.RedisRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RedisRateLimiter redisRateLimiter;
    private final HttpServletRequest httpServletRequest;

    @Around("@annotation(com.example.SpringSecurity.annotation.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String clientIp = httpServletRequest.getRemoteAddr();
        String redisKey = "ratelimit:"+clientIp+":"+method.getName();

        boolean allowed = redisRateLimiter.isAllowed(redisKey, rateLimit.limit(), rateLimit.timeWindowSeconds());

        if (!allowed) throw new RateLimitExceedException("Too Many Requests. Please try again.");

        return joinPoint.proceed();
    }

}