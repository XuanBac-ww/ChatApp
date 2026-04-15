package com.example.SpringSecurity.model;

import com.example.SpringSecurity.model.Abstraction.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@Audited
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
}
