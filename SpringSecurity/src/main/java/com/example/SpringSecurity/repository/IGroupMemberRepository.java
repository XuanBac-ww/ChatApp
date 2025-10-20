package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Group;
import com.example.SpringSecurity.model.GroupMember;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IGroupMemberRepository extends IBaseRepository<GroupMember,Long> {
    // Kiểm tra xem user có phải là thành viên của group không
    boolean existsByUserAndGroup(User user, Group group);

    // Tìm kiếm thành viên theo user và group (hữu ích để kiểm tra vai trò)
    Optional<GroupMember> findByUserAndGroup(User user, Group group);
}
