package com.example.SpringSecurity.service.chatMessage;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.message.ChatMessageDTO;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.ChatGroup;
import com.example.SpringSecurity.model.ChatMessage;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IChatGroupRepository;
import com.example.SpringSecurity.repository.IChatMessageRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService implements IChatMessageService{
    private final IUserRepository userRepository;
    private final IChatMessageRepository chatMessageRepository;
    private final IChatGroupRepository chatGroupRepository;

    @Transactional
    @Override
    public ChatMessageDTO processPrivateMessage(ChatMessageDTO messageDTO, Long currentUserid) {
        User sender = userRepository.findById(currentUserid)
                .orElseThrow(() -> new AppException("Sender not found"));
        User recipient = userRepository.findByFullName(messageDTO.getRecipientFullName())
                .orElseThrow(() -> new AppException("Recipient not found"));

        if (sender.getId().equals(recipient.getId())) {
            throw new AppException("Cannot send message to yourself");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .content(messageDTO.getContent())
                .sender(sender)
                .recipient(recipient)
                .build();

        ChatMessage saveMessage = chatMessageRepository.save(chatMessage);

        messageDTO.setId(saveMessage.getId());
        messageDTO.setSenderFullName(sender.getFullName());
        messageDTO.setTimestamp(saveMessage.getCreatedAt());
        return messageDTO;
    }

    @Transactional
    @Override
    public ChatMessageDTO processGroupMessage(ChatMessageDTO messageDTO, Long currentUserId) {

        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException("Sender not found"));

        boolean isMember = chatGroupRepository.isUserMemberOfGroup(currentUserId, messageDTO.getGroupId());

        if (!isMember) {
            throw new AppException("User is not a member of this group or group does not exist");
        }

        ChatGroup group = chatGroupRepository.findById(messageDTO.getGroupId())
                .orElseThrow(() -> new AppException("Group not found"));

        ChatMessage entity = ChatMessage.builder()
                .content(messageDTO.getContent())
                .sender(sender)
                .chatGroup(group)
                .build();

        ChatMessage savedEntity = chatMessageRepository.save(entity);

        messageDTO.setId(savedEntity.getId());
        messageDTO.setSenderFullName(sender.getFullName());
        messageDTO.setTimestamp(savedEntity.getCreatedAt());
        return messageDTO;
    }
}
