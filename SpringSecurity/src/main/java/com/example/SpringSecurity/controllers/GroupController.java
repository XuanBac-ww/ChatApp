package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.request.group.CreateGroupRequest;
import com.example.SpringSecurity.dto.request.group.UpdateGroupRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.group.GroupDTO;
import com.example.SpringSecurity.dto.response.groupMember.GroupMemberDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.group.IGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final IGroupService groupService;
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<GroupMemberDTO> createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupService.createGroup(createGroupRequest,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<GroupDTO> updateGroupInfo(@RequestBody @Valid UpdateGroupRequest updateGroupRequest, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupService.updateGroupInfo(updateGroupRequest,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{groupId}")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<?> deleteGroup(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupService.deleteGroup(groupId,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<List<GroupDTO>> getMyGroups(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return groupService.getMyGroups(currentUser.getUserId());
    }
}
