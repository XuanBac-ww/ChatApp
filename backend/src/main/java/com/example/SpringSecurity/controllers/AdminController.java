package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IUserService userService;

    @RateLimit(limit = 5,timeWindowSeconds = 60)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public PageResponse<UserDTO> getAllUser(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUser(page,size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all/deleted")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public PageResponse<UserDTO> getAllDeletedUser(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return userService.getDeletedUsers(page,size);
    }

    @RateLimit(limit = 5,timeWindowSeconds = 60)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-account")
    public ApiResponse<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.deleteUser(currentUser.getUserId());
    }



}
