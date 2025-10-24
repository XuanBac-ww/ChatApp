package com.example.SpringSecurity.dto.response.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private String senderFullName;
    private Instant timestamp;
    private String content;
    private String recipientFullName;
    private Long groupId;
}
