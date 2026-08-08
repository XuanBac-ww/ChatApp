package com.example.SpringSecurity.model;

import com.example.SpringSecurity.enums.ConversationType;
import com.example.SpringSecurity.model.Abstraction.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Builder
public class Conversation extends BaseEntity {

    @Column(length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type;

    // Khóa xác định cặp user cho DIRECT_MESSAGE, dạng "minUserId_maxUserId". Null với conversation nhóm.
    // Unique constraint chặn 2 direct conversation trùng nhau khi có race condition (MySQL cho phép nhiều NULL trong unique index).
    @Column(name = "direct_pair_key", unique = true, length = 40)
    private String directPairKey;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationParticipant> participants;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages;
}