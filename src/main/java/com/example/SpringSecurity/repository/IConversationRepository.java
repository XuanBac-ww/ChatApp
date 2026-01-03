package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Conversation;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IConversationRepository extends IBaseRepository<Conversation,Long> {

    @Query("SELECT c FROM Conversation c " +
            "JOIN c.participants p1 " +
            "JOIN c.participants p2 " +
            "WHERE c.type = 'DIRECT_MESSAGE' " +
            "AND p1.user.id = :userId1 " +
            "AND p2.user.id = :userId2")
    Optional<Conversation> findDirectConversationBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );

    @Query("SELECT DISTINCT c FROM Conversation c " +
            "LEFT JOIN FETCH c.participants p " +
            "LEFT JOIN FETCH p.user u " +
            "WHERE c.id IN (SELECT cp.conversation.id FROM ConversationParticipant cp WHERE cp.user.id = :userId)")
    List<Conversation> findConversationsByUserId(@Param("userId") Long userId);
}
