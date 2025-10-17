//package com.example.SpringSecurity.model;
//
//import com.example.SpringSecurity.enums.ChatType;
//import com.example.SpringSecurity.model.Abstraction.SoftDelete;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.Filter;
//import org.hibernate.annotations.SQLDelete;
//import org.hibernate.annotations.SQLRestriction;
//
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "chat_room")
//@SQLDelete(sql = "UPDATE chat_room SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
//@SQLRestriction("deleted = false")
//@Filter(name = "deletedFilter", condition = "deleted = :isDeleted")
//public class ChatRoom extends SoftDelete {
//
//    @Column(nullable = false)
//    private String nameRoom;
//
//    @Enumerated(EnumType.STRING)
//    private ChatType chatType;
//
//    @ManyToMany
//    @JoinTable(
//            name = "chat_room_members",
//            joinColumns = @JoinColumn(name = "chat_room_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private List<User> members;
//
//    @OneToMany(mappedBy = "chatRoom")
//    private List<Message> messages;
//}
