package com.example.SpringSecurity.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails);

    Long extractUserId(String token);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    long getExpirationTime();

    boolean isTokenValid(String token, UserDetails userDetails);

    Date extractExpiration(String token);
}
