package com.example.SpringSecurity.aspect;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisRateLimiter redisRateLimiter;
    private final HttpServletRequest request;

    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String KEY_PREFIX = "ratelimit:";

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateRedisKey(joinPoint);
        boolean isAllowed = redisRateLimiter.isAllowed(
                key,
                rateLimit.limit(),
                rateLimit.timeWindowSeconds()
        );

        if (!isAllowed) {
            throw new RateLimitExceedException("Too Many Requests. Please try again.");
        }
        return joinPoint.proceed();
    }


    private String generateRedisKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String identifier = resolveIdentifier();

        return KEY_PREFIX + identifier + ":" + methodName;
    }

    private String resolveIdentifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isValidUser(auth)) {
            return auth.getName();
        }
        return getClientIp();
    }

    private boolean isValidUser(Authentication auth) {
        return auth != null &&
                auth.isAuthenticated() &&
                !ANONYMOUS_USER.equals(auth.getName());
    }

    private String getClientIp() {
        String remoteAddr = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            return request.getRemoteAddr();
        }
        return remoteAddr.split(",")[0].trim();
    }
}