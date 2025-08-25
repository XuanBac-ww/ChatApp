package com.example.SpringSecurity.controller;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/me")
    public ApiResponse<UserDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.getUserInfo(currentUser.getUserId());
    }

    @GetMapping("/all")
    public ApiResponse<List<UserDTO>> getAllUser() {
        return userService.getAllUser();
    }
}
