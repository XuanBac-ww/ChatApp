package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.conversation.ConversationDetailDTO;
import com.example.SpringSecurity.dto.response.conversation.ConversationPreviewDTO;
import com.example.SpringSecurity.dto.response.user.ParticipantDTO;
import com.example.SpringSecurity.enums.ConversationType;
import com.example.SpringSecurity.model.ChatMessage;
import com.example.SpringSecurity.model.Conversation;
import com.example.SpringSecurity.model.ConversationParticipant;
import com.example.SpringSecurity.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ConversationMapper {

    public abstract ConversationDetailDTO mapToConversationDetailDTO(Conversation convo);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "name")
    @Mapping(source = "user.profileImage.url", target = "avatar")
    @Mapping(source = "role", target = "role")
    public abstract ParticipantDTO mapToParticipantDTO(ConversationParticipant participant);


    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "type", source = "conversation.type")
    @Mapping(target = "lastMessage", expression = "java(lastMessage != null ? lastMessage.getContent() : \"Chưa có tin nhắn\")")
    @Mapping(target = "lastMessageTimestamp", source = "lastMessage.createdAt")
    // Các trường sau sẽ được xử lý ở @AfterMapping
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "userId", ignore = true)
    public abstract ConversationPreviewDTO toPreviewDTO(Conversation conversation,
                                                        ChatMessage lastMessage,
                                                        @Context User currentUser);

    @AfterMapping
    protected void enrichDTO(@MappingTarget ConversationPreviewDTO dto,
                             Conversation conversation,
                             @Context User currentUser) {

        String name = conversation.getName();
        String avatar = null;
        Long userId = null;

        if (conversation.getType() == ConversationType.DIRECT_MESSAGE) {
            User partner = getChatPartner(conversation, currentUser);
            name = partner.getFullName();
            userId = partner.getId();
            avatar = (partner.getProfileImage() != null) ? partner.getProfileImage().getUrl() : null;
        }

        dto.setName(name);
        dto.setAvatar(avatar);
        dto.setUserId(userId);
    }

    private User getChatPartner(Conversation convo, User currentUser) {
        if (convo.getParticipants() == null) return currentUser;

        return convo.getParticipants().stream()
                .map(ConversationParticipant::getUser)
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .findFirst()
                .orElse(currentUser);
    }

}
