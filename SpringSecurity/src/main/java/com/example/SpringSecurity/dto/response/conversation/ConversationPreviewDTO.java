package com.example.SpringSecurity.dto.response.conversation;

import com.example.SpringSecurity.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationPreviewDTO {
    private Long conversationId;
    private ConversationType type;
    private Long userId;
    private String name;
    private String avatar;
    private String lastMessage;
    private Date lastMessageTimestamp;
}
