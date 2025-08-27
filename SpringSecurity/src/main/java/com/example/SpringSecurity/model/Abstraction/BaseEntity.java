package com.example.SpringSecurity.model.Abstraction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;  // first time created

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;  //   updated after created

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;    // created by user

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;    // updated by user
}