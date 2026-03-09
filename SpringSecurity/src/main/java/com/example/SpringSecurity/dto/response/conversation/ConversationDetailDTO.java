package com.example.SpringSecurity.dto.response.conversation;

import com.example.SpringSecurity.dto.response.user.ParticipantDTO;
import com.example.SpringSecurity.enums.ConversationType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ConversationDetailDTO {

    private Long id;
    private ConversationType type;
    private String name;
    private Set<ParticipantDTO> participants;
}
