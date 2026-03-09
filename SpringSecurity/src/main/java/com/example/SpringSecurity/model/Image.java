package com.example.SpringSecurity.model;

import com.example.SpringSecurity.model.Abstraction.SoftDelete;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Image extends SoftDelete {

    @Column(nullable = false, length = 512)
    private String url;

    @NaturalId(mutable = true)
    @Column(nullable = false, unique = true, length = 64)
    private String hash;

    @Column(columnDefinition = "TEXT")
    private String data; // size image

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;
}
