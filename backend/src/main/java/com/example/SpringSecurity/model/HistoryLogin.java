package com.example.SpringSecurity.model;

import com.example.SpringSecurity.model.Abstraction.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;


import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history_login")
public class HistoryLogin extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
