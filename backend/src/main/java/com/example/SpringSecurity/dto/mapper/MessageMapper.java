package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.message.MessageDTO;
import com.example.SpringSecurity.dto.response.user.SenderDTO;
import com.example.SpringSecurity.model.ChatMessage;
import com.example.SpringSecurity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "createdAt", target = "timestamp")
    @Mapping(source = "conversation.id", target = "conversationId")
    MessageDTO mapToMessageDTO(ChatMessage message);


    @Mapping(source = "id", target = "userId")
    @Mapping(source = "fullName", target = "name")
    @Mapping(source = "profileImage.url", target = "avatar")
    SenderDTO mapSenderToSenderDTO(User sender);
}
