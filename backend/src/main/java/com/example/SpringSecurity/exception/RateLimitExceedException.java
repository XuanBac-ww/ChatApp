package com.example.SpringSecurity.exception;

public class RateLimitExceedException extends RuntimeException {
    public RateLimitExceedException(String message) {
        super(message);
    }
}
