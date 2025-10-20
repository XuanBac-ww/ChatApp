package com.example.SpringSecurity.service.groupMember;

import com.example.SpringSecurity.dto.response.api.ApiResponse;

public interface IGroupMemberService {
    ApiResponse<?> addMemberToGroup();
    ApiResponse<?> removeMember();
    ApiResponse<?> leaveGroup();
    ApiResponse<?> getMemberGroups();
}
