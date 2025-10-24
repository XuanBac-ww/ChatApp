package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.request.groupMember.AddMemberRequestDTO;
import com.example.SpringSecurity.dto.request.groupMember.KickMemberRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.groupMember.GroupMemberDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.groupMember.IGroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group-member")
@RequiredArgsConstructor
public class GroupMemberController {

    private final IGroupMemberService groupMemberService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add/{groupId}")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<GroupMemberDTO> addMemberToGroup(@PathVariable Long groupId,
                                                     @RequestBody AddMemberRequestDTO request,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupMemberService.addMemberToGroup(groupId,request,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/remove/{groupId}")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<?> removeMember(@PathVariable Long groupId,
                                                     @RequestBody KickMemberRequest request,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupMemberService.removeMember(groupId,request,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/leave/{groupId}")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<?> leaveGroup(@PathVariable Long groupId,
                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupMemberService.leaveGroup(groupId,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{groupId}")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<List<GroupMemberDTO>> getMemberGroups(@PathVariable Long groupId,
                                                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupMemberService.getMemberGroups(groupId,currentUser.getUserId());
    }
}
