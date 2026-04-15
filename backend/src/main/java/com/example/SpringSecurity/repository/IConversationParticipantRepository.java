package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Conversation;
import com.example.SpringSecurity.model.ConversationParticipant;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IConversationParticipantRepository extends IBaseRepository<ConversationParticipant,Long> {
    // user co phai thanh vien cua 1 conversation khong
    Optional<ConversationParticipant> findByConversationAndUser(Conversation conversation, User user);

}
