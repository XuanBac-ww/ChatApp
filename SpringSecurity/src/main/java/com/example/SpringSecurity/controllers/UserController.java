package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.request.user.NumberPhoneRequest;
import com.example.SpringSecurity.dto.request.user.UserUpdateRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.dto.response.user.UserSearchDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<UserDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.getUserInfo(currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{fullName}")
    public ApiResponse<UserDTO> findUserByFullName(@PathVariable String fullName) {
        return userService.findUserByFullName(fullName);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update-account")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<UserDTO> updateAccount(@RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.updateUser(userUpdateRequest,currentUser.getUserId());
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/search")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<UserSearchDTO> searchUsers(@Valid @RequestBody NumberPhoneRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
       return userService.searchUsers(request.getNumberPhone(), userDetails.getUserId());
    }
}
