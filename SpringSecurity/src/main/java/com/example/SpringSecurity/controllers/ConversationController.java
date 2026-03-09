package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.request.conversation.StartDirectChatRequestDTO;
import com.example.SpringSecurity.dto.request.message.SendMessageRequestDTO;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.conversation.ConversationDetailDTO;
import com.example.SpringSecurity.dto.response.conversation.ConversationPreviewDTO;
import com.example.SpringSecurity.dto.response.message.MessageDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.conversation.IConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final IConversationService conversationService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<List<ConversationPreviewDTO>> getMyConversations(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return conversationService.getConversationForUser(currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{conversationId}/messages")
    public ApiResponse<List<MessageDTO>> getMessages(@PathVariable Long conversationId,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        return conversationService.getMessagesForConversation(conversationId,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{conversationId}/messages")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    public ApiResponse<MessageDTO> sendMessage(@PathVariable Long conversationId,
                                               @Valid @RequestBody SendMessageRequestDTO messageRequest,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return conversationService.sendMessage(conversationId,messageRequest,userDetails.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/direct")
    public ApiResponse<ConversationDetailDTO> startDirectConversation(@Valid @RequestBody StartDirectChatRequestDTO request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return conversationService.startOrGetDirectConversation(request.getRecipientId(),userDetails.getUserId());
    }

}
