package com.example.SpringSecurity.model;

import com.example.SpringSecurity.enums.FriendshipStatus;
import com.example.SpringSecurity.model.Abstraction.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friendship")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friendship extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus friendshipStatus;
}
