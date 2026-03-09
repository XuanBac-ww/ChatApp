package com.example.SpringSecurity.service.conversation;

import com.example.SpringSecurity.dto.request.message.SendMessageRequestDTO;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.conversation.ConversationDetailDTO;
import com.example.SpringSecurity.dto.response.conversation.ConversationPreviewDTO;
import com.example.SpringSecurity.dto.response.message.MessageDTO;

import java.util.List;

public interface IConversationService {
    ApiResponse<List<ConversationPreviewDTO>> getConversationForUser(Long userId);

    ApiResponse<List<MessageDTO>> getMessagesForConversation(Long conversationId, Long userId);

    ApiResponse<MessageDTO> sendMessage(Long conversationId, SendMessageRequestDTO request, Long userId);

    ApiResponse<ConversationDetailDTO> startOrGetDirectConversation(Long recipientId, Long userId);

}
