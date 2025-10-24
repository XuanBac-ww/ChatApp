package com.example.SpringSecurity.service.chatMessage;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.message.ChatMessageDTO;
import jakarta.transaction.Transactional;

public interface IChatMessageService {
    @Transactional
    ChatMessageDTO processPrivateMessage(ChatMessageDTO messageDTO, Long currentUserid);

    @Transactional
    ChatMessageDTO processGroupMessage(ChatMessageDTO messageDTO, Long currentUserId);
}
