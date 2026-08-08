package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Conversation;
import com.example.SpringSecurity.model.ConversationParticipant;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IConversationParticipantRepository extends IBaseRepository<ConversationParticipant,Long> {
    // user co phai thanh vien cua 1 conversation khong
    Optional<ConversationParticipant> findByConversationAndUser(Conversation conversation, User user);

}
