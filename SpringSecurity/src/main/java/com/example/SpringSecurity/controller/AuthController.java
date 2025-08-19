package com.example.SpringSecurity.controller;

import com.example.SpringSecurity.dtos.auth.LoginResponse;
import com.example.SpringSecurity.dtos.auth.LoginUserRequest;
import com.example.SpringSecurity.dtos.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.service.HistoryLogin.IHistoryLoginService;
import com.example.SpringSecurity.service.JwtService;
import com.example.SpringSecurity.service.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final JwtService jwtService;
    private final IHistoryLoginService historyLoginService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest registerUser) {
        User user = authService.signup(registerUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserRequest loginUser) {
        User user = authService.authenticate(loginUser);
        String jwt = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setRefreshToken(historyLoginService.createRefreshToken(user.getId()));
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> payload) {
        String refreshToken = payload.get("refreshToken");
        String newAccessToken = historyLoginService.handleRefreshToken(refreshToken);

        return new ResponseEntity<>(Map.of("token", newAccessToken), HttpStatus.OK);
    }
}
