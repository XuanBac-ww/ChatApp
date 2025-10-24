package com.example.SpringSecurity.service.group;

import com.example.SpringSecurity.dto.request.group.CreateGroupRequest;
import com.example.SpringSecurity.dto.request.group.UpdateGroupRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.group.GroupDTO;
import com.example.SpringSecurity.dto.response.groupMember.GroupMemberDTO;
import com.example.SpringSecurity.model.GroupMember;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IGroupService {
    @Transactional
    ApiResponse<GroupMemberDTO> createGroup(CreateGroupRequest createGroupRequest, Long ownerId);

    @Transactional
    ApiResponse<GroupDTO> updateGroupInfo(UpdateGroupRequest updateGroupRequest, Long userRequestId);

    @Transactional
    ApiResponse<?> deleteGroup(Long groupId,Long ownerId);

    ApiResponse<List<GroupDTO>> getMyGroups(Long userId);
}
