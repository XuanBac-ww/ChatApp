package com.example.SpringSecurity.service.groupMember;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupMemberService implements IGroupMemberService {
    @Override
    public ApiResponse<?> addMemberToGroup() {
        return null;
    }

    @Override
    public ApiResponse<?> removeMember() {
        return null;
    }

    @Override
    public ApiResponse<?> leaveGroup() {
        return null;
    }

    @Override
    public ApiResponse<?> getMemberGroups() {
        return null;
    }
}
