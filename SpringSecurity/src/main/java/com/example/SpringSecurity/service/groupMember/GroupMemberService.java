package com.example.SpringSecurity.service.groupMember;

import com.example.SpringSecurity.dto.mapper.GroupMemberMapper;
import com.example.SpringSecurity.dto.request.groupMember.AddMemberRequestDTO;
import com.example.SpringSecurity.dto.request.groupMember.KickMemberRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.groupMember.GroupMemberDTO;
import com.example.SpringSecurity.enums.GroupRole;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.ChatGroup;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.model.GroupMember;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IChatGroupRepository;
import com.example.SpringSecurity.repository.IFriendshipRepository;
import com.example.SpringSecurity.repository.IGroupMemberRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupMemberService implements IGroupMemberService {
    private final IGroupMemberRepository groupMemberRepository;
    private final IChatGroupRepository groupRepository;
    private final IUserRepository userRepository;
    private final IFriendshipRepository friendshipRepository;
    private final GroupMemberMapper groupMemberMapper;

    @Override
    public ApiResponse<GroupMemberDTO> addMemberToGroup(Long groupId, AddMemberRequestDTO request, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng hiện tại"));

        User userToAdd = userRepository.findById(request.getUserIdToAdd())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng cần thêm"));

        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Không tìm thấy nhóm"));
        if (!groupMemberRepository.existsByUserAndGroup(currentUser, group)) {
            return new ApiResponse<>(200,false,"Bạn không phải là thành viên của nhóm này",null);
        }
        if (groupMemberRepository.existsByUserAndGroup(userToAdd, group)) {
            return new ApiResponse<>(200,false,"Người này đã ở trong nhóm",null);
        }
        Optional<Friendship> isFriend = friendshipRepository.findFriendshipBetweenUsers(currentUser, userToAdd);
        if (isFriend.isEmpty()) {
            return new ApiResponse<>(200,false,"Bạn chỉ có thể thêm bạn bè của mình vào nhóm",null);
        }

        GroupMember newMember = GroupMember.builder()
                .user(userToAdd)
                .group(group)
                .role(GroupRole.GR_USER)
                .build();
        groupMemberRepository.save(newMember);
        return new ApiResponse<>(200,true,"Thêm " + request.getUserIdToAdd() + "Thành Công",groupMemberMapper.convertToGroupMemberDTO(newMember));
    }

    @Override
    public ApiResponse<?> removeMember(Long groupId, KickMemberRequest request, Long currentId) {

        if (request.getUserIdToKick().equals(currentId)) {
            return new ApiResponse<>(400, false, "Bạn không thể tự đuổi chính mình", null);
        }
        GroupMember currentUserMembership = groupMemberRepository.findByUserIdAndGroupId(currentId, groupId)
                .orElseThrow(() -> new AppException("Bạn không phải là thành viên của nhóm này"));
        if (currentUserMembership.getRole() != GroupRole.GR_ADMIN) {
            return new ApiResponse<>(403, false, "Chỉ ADMIN mới có quyền đuổi thành viên", null);
        }
        GroupMember memberToKick = groupMemberRepository.findByUserIdAndGroupId(request.getUserIdToKick(), groupId)
                .orElseThrow(() -> new AppException("Người này không phải là thành viên của nhóm"));
        if (memberToKick.getRole() == GroupRole.GR_ADMIN) {
            return new ApiResponse<>(403, false, "Bạn không thể đuổi một ADMIN khác", null);
        }
        groupMemberRepository.delete(memberToKick);
        return new ApiResponse<>(200, true, "Đã đuổi thành viên thành công", null);
    }

    @Override
    public ApiResponse<?> leaveGroup(Long groupId, Long currentUserId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Không tìm thấy nhóm"));
        GroupMember memberLeaving = groupMemberRepository.findByUserIdAndGroupId(currentUserId, groupId)
                .orElseThrow(() -> new AppException("Bạn không phải là thành viên của nhóm này"));
        if (memberLeaving.getRole() == GroupRole.GR_ADMIN) {
            List<GroupMember> otherMembers = groupMemberRepository.findByGroupIdAndRole(groupId, GroupRole.GR_USER);

            if (!otherMembers.isEmpty()) {
                otherMembers.sort(Comparator.comparing(GroupMember::getCreatedAt));
                GroupMember newAdmin = otherMembers.get(0);
                newAdmin.setRole(GroupRole.GR_ADMIN);
                groupMemberRepository.save(newAdmin);

                group.setOwner(newAdmin.getUser());
                groupRepository.save(group);

                groupMemberRepository.delete(memberLeaving);

                return new ApiResponse<>(200, true, "Rời nhóm thành công. Đã chỉ định ADMIN mới", null);
            }
            groupRepository.delete(group);
            return new ApiResponse<>(200, true, "Rời nhóm thành công. Nhóm đã được giải tán", null);
        }
        groupMemberRepository.delete(memberLeaving);
        return new ApiResponse<>(200, true, "Rời nhóm thành công", null);
    }

    @Override
    public ApiResponse<List<GroupMemberDTO>> getMemberGroups(Long groupId, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException("Người dùng không tồn tại"));

        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Nhóm không tồn tại"));

        if (!groupMemberRepository.existsByUserAndGroup(currentUser, group)) {
            return new ApiResponse<>(403, false, "Bạn không phải là thành viên của nhóm này", null);
        }

        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<GroupMemberDTO> groupMemberDTO = members.stream()
                .map(groupMemberMapper::convertToGroupMemberDTO)
                .toList();
        return new ApiResponse<>(200, true, "Lấy danh sách thành viên thành công", groupMemberDTO);
    }
}
