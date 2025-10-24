package com.example.SpringSecurity.service.group;

import com.example.SpringSecurity.dto.mapper.GroupMapper;
import com.example.SpringSecurity.dto.mapper.GroupMemberMapper;
import com.example.SpringSecurity.dto.request.group.CreateGroupRequest;
import com.example.SpringSecurity.dto.request.group.UpdateGroupRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.group.GroupDTO;
import com.example.SpringSecurity.dto.response.groupMember.GroupMemberDTO;
import com.example.SpringSecurity.enums.GroupRole;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.ChatGroup;
import com.example.SpringSecurity.model.GroupMember;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IChatGroupRepository;
import com.example.SpringSecurity.repository.IGroupMemberRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService implements IGroupService {
    private final IUserRepository userRepository;
    private final IChatGroupRepository groupRepository;
    private final IGroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;
    @Override
    @Transactional
    public ApiResponse<GroupMemberDTO> createGroup(CreateGroupRequest createGroupRequest, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new AppException("User Not Found"));
        ChatGroup group = ChatGroup.builder()
                .groupName(createGroupRequest.getGroupName())
                .owner(owner)
                .build();
        ChatGroup saveGroup = groupRepository.save(group);
        GroupMember groupMember = GroupMember.builder()
                .user(owner)
                .group(saveGroup)
                .role(GroupRole.GR_ADMIN)
                .build();
        groupMemberRepository.save(groupMember);
        return new ApiResponse<>(200,true,"Tạo Group Thành Công",groupMemberMapper.convertToGroupMemberDTO(groupMember));
    }

    @Override
    public ApiResponse<GroupDTO> updateGroupInfo(UpdateGroupRequest updateGroupRequest, Long userRequestId) {
        ChatGroup group = groupRepository.findById(updateGroupRequest.getGroupId())
                .orElseThrow(() -> new AppException("Group Not Found"));
        User currentUser = userRepository.findById(userRequestId)
                .orElseThrow(() ->  new AppException("User Not Found"));
        if(!groupMemberRepository.existsByUserAndGroup(currentUser, group)) {
            return new ApiResponse<>(200,false,"Ban khong phai la thanh vien cua nhom",null);
        }

        group.setGroupName(updateGroupRequest.getGroupName());
        GroupDTO groupDTO = groupMapper.convertToGroupDTO(group);
        return new ApiResponse<>(200,true,"Cap Nhat ten nhom thanh cong",groupDTO);
    }

    @Override
    public ApiResponse<?> deleteGroup(Long groupId, Long ownerId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group Not Found"));
        if(!group.getOwner().getId().equals(ownerId)) {
            return new ApiResponse<>(200,false,"Chi co Admin moi co quyen xoa",null);
        }
        groupRepository.delete(group);
        return new ApiResponse<>(200,true,"Xoa Group Thanh Cong",null);
    }

    @Override
    public ApiResponse<List<GroupDTO>> getMyGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));

        List<GroupDTO> myGroupsList = user.getGroupMemberships()
                .stream()
                .map(groupMember -> {
                    ChatGroup group = groupMember.getGroup();

                    GroupDTO groupDTO = groupMapper.convertToGroupDTO(group);
                    if (group.getOwner() != null) {
                        groupDTO.setOwnerId(group.getOwner().getId());
                    }
                    return groupDTO;
                })
                .collect(Collectors.toList());
        return new ApiResponse<>(200,true,"Lay Danh sach Thanh cong",myGroupsList);
    }
}
