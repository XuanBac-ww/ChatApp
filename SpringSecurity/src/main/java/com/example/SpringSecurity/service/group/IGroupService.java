package com.example.SpringSecurity.service.group;

import com.example.SpringSecurity.dto.request.group.CreateGroupRequest;
import com.example.SpringSecurity.dto.request.group.UpdateGroupRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IGroupService {
    @Transactional
    ApiResponse<?> createGroup(CreateGroupRequest createGroupRequest,Long ownerId);

    @Transactional
    ApiResponse<?> updateGroupInfo(UpdateGroupRequest updateGroupRequest, Long userRequestId);

    @Transactional
    ApiResponse<?> deleteGroup(Long groupId,Long ownerId);

    ApiResponse<?> getMyGroups(Long userId);
}
