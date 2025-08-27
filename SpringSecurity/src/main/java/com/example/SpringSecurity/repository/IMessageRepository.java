package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Message;
import com.example.SpringSecurity.repository.Abstraction.ISoftDeleteRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface IMessageRepository extends ISoftDeleteRepository<Message, Long>, RevisionRepository<Message, Long, Integer> {
}