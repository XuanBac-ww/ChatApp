package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.enums.GroupRole;
import com.example.SpringSecurity.model.ChatGroup;
import com.example.SpringSecurity.model.GroupMember;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGroupMemberRepository extends IBaseRepository<GroupMember,Long> {

    boolean existsByUserAndGroup(User user, ChatGroup group);

    Optional<GroupMember> findByUserIdAndGroupId(Long currentId, Long groupId);

    List<GroupMember> findByGroupId(Long groupId);

    List<GroupMember> findByGroupIdAndRole(Long groupId, GroupRole groupRole);

}
