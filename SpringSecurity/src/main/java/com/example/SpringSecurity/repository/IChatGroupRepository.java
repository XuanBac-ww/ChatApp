package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.ChatGroup;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IChatGroupRepository extends IBaseRepository<ChatGroup,Long> {
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM ChatGroup g JOIN g.members m " +
            "WHERE g.id = :groupId AND m.id = :userId")
    boolean isUserMemberOfGroup(Long userId, Long groupId);

}
