package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.request.friend.FriendRequest;
import com.example.SpringSecurity.dto.request.user.NumberPhoneRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.friend.FriendShipResponseDTO;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.friendship.IFriendshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friend-ship")
@RequiredArgsConstructor
public class FriendShipController {

    private final IFriendshipService friendshipService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/send-request")
    @RateLimit(limit = 5, timeWindowSeconds = 60)
    public ApiResponse<FriendShipResponseDTO> sendAddFriend(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody @Valid FriendRequest request) {
        return friendshipService.sendAddFriend(currentUser.getUserId(), request);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/accept-request")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<?> acceptFriendRequest(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestBody @Valid FriendRequest request) {
        return friendshipService.acceptFriendRequest(currentUser.getUserId(), request.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/reject-request")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<?> rejectFriendRequest(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestBody @Valid FriendRequest request) {
        return friendshipService.rejectFriendRequest(currentUser.getUserId(), request.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public PageResponse<?> getAllFriend(@AuthenticationPrincipal CustomUserDetails currentUser,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return friendshipService.getAllFriend(currentUser.getUserId(),page,size );
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/pending-request")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public PageResponse<?> getPendingFriendRequest(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return friendshipService.getPendingFriendRequest(currentUser.getUserId(),page,size);
    }
}
