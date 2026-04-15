package com.example.SpringSecurity.dto.response.message;

import com.example.SpringSecurity.dto.response.user.SenderDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private SenderDTO sender;
    private Long conversationId;
}
