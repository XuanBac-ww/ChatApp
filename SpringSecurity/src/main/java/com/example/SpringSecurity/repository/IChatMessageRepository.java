package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.ChatMessage;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChatMessageRepository extends IBaseRepository<ChatMessage,Long> {
}
