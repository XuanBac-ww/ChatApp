package com.example.SpringSecurity.model.Abstraction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date createdAt;  // first time created


    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;  //   updated after created

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;    // created by user

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;    // updated by user
}