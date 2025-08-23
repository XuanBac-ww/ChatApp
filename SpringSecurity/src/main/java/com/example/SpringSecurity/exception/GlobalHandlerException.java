package com.example.SpringSecurity.exception;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(Exception ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                      400,
                        false,
                        ex.getMessage(),
                        null
                ));
    }


    // Lỗi chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        500,
                        false,
                        ex.getMessage(),
                        null
                ));
    }


    // 401 - Unauthorized
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        false,
                        "Unauthorized: " + ex.getMessage(),
                        null
                ));
    }

    // 403 - Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(
                        HttpStatus.FORBIDDEN.value(),
                        false,
                        "Forbidden: " + ex.getMessage(),
                        null
                ));
    }

    // 404 - API không tồn tại
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        false,
                        "API not found: " + ex.getRequestURL(),
                        null
                ));
    }
}
