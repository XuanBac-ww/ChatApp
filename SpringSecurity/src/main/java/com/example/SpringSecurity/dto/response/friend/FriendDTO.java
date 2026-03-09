package com.example.SpringSecurity.dto.response.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private Long id;
    private String userName;
    private String profileImage;
}
