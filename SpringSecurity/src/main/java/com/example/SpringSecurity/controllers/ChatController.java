package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.dto.response.message.ChatMessageDTO;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.chatMessage.IChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final IChatMessageService chatMessageService;

    @MessageMapping("/private-message")
    public void handlePrivateMessage(ChatMessageDTO dto,
                                     @AuthenticationPrincipal CustomUserDetails currentUser) {

        ChatMessageDTO message = chatMessageService.processPrivateMessage(dto, currentUser.getUserId());

        messagingTemplate.convertAndSendToUser(
                message.getRecipientFullName(),
                "/queue/private",
                message
        );
    }

    @MessageMapping("/group-message")
    public void handleGroupMessage(ChatMessageDTO dto,
                                   @AuthenticationPrincipal CustomUserDetails currentUser) {

        ChatMessageDTO savedDto = chatMessageService.processGroupMessage(dto, currentUser.getUserId());

        String destination = "/topic/group/" + savedDto.getGroupId();
        messagingTemplate.convertAndSend(destination, savedDto);
    }
}
