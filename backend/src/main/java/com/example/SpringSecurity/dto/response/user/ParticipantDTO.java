package com.example.SpringSecurity.dto.response.user;

import com.example.SpringSecurity.enums.ConversationRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantDTO {

    private Long userId;
    private String name;
    private String avatar;
    private ConversationRole role;
}
