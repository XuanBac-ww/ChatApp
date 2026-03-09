package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.ChatMessage;
import com.example.SpringSecurity.model.Conversation;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IChatMessageRepository extends IBaseRepository<ChatMessage,Long> {


    List<ChatMessage> findAllByConversationIdOrderByCreatedAtAsc(Long conversationId);

    // Lấy tin nhắn cuối cùng
    Optional<ChatMessage> findTopByConversationOrderByCreatedAtDesc(Conversation conversation);
}
