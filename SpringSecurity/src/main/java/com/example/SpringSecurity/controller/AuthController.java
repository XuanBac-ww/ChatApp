package com.example.SpringSecurity.controller;

import com.example.SpringSecurity.dtos.LoginResponse;
import com.example.SpringSecurity.dtos.LoginUser;
import com.example.SpringSecurity.dtos.RegisterUser;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.service.AuthService;
import com.example.SpringSecurity.service.JwtService;
import com.example.SpringSecurity.service.RefreshTokenService;
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
    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUser registerUser) {
        User user = authService.signup(registerUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUser loginUser) {
        User user = authService.authenticate(loginUser);
        String jwt = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setRefreshToken(refreshTokenService.createRefreshToken(user.getId()));
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String,String> payload) {
        String refreshToken = payload.get("refreshToken");
        String newAccessToken = refreshTokenService.handleRefreshToken(refreshToken);

        return new ResponseEntity<>(Map.of("token", newAccessToken), HttpStatus.OK);
    }
}
