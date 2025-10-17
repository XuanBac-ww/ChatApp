//package com.example.SpringSecurity.model;
//
//import com.example.SpringSecurity.model.Abstraction.SoftDelete;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.Filter;
//import org.hibernate.annotations.SQLDelete;
//import org.hibernate.annotations.SQLRestriction;
//
//@Entity
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "messages")
//@SQLDelete(sql = "UPDATE messages SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
//@SQLRestriction("deleted = false")
//@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
//public class Message extends SoftDelete {
//
//    @ManyToOne
//    @JoinColumn(name = "sender_id")
//    private User sender;
//
//    @ManyToOne
//    @JoinColumn(name = "chat_room_id")
//    private ChatRoom chatRoom;
//
//    @Column(nullable = false,columnDefinition = "TEXT")
//    private String content;
//}
