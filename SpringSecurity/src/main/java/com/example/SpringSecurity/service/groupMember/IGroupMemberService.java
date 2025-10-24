package com.example.SpringSecurity.service.groupMember;

import com.example.SpringSecurity.dto.request.groupMember.AddMemberRequestDTO;
import com.example.SpringSecurity.dto.request.groupMember.KickMemberRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.groupMember.GroupMemberDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IGroupMemberService {

    @Transactional
    ApiResponse<GroupMemberDTO> addMemberToGroup(Long groupId, AddMemberRequestDTO request, Long currentUserId);

    @Transactional
    ApiResponse<?> removeMember(Long groupId, KickMemberRequest request, Long currentId);

    @Transactional
    ApiResponse<?> leaveGroup(Long groupId,Long currentUserId);

    ApiResponse<List<GroupMemberDTO>> getMemberGroups(Long groupId, Long currentUserId);
}
