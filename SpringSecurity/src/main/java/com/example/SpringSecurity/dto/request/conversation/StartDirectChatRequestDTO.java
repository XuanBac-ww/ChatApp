package com.example.SpringSecurity.dto.request.conversation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartDirectChatRequestDTO {

    @NotNull
    private Long recipientId;
}
