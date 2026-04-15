package com.example.SpringSecurity.dto.response.friend;

import com.example.SpringSecurity.dto.response.user.UserSearchDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendShipResponseDTO {
    private Long friendshipId;
    private String status;
    private UserSearchDTO requester;
    private UserSearchDTO addressee;
}
