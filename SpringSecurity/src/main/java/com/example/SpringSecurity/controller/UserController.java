package com.example.SpringSecurity.controller;

import com.example.SpringSecurity.dto.request.user.UserUpdateRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/me")
    public ApiResponse<UserDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.getUserInfo(currentUser.getUserId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public PageResponse<UserDTO> getAllUser( @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUser(page,size);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/deleteAccount")
    public ApiResponse<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.deleteUser(currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update")
    public ApiResponse<UserDTO> updateAccount(@RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.updateUser(userUpdateRequest,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all/deleted")
    public PageResponse<UserDTO> getAllDeletedUser( @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return userService.getDeletedUsers(page,size);
    }


}
