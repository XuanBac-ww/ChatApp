package com.example.SpringSecurity.dto.response.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestResponse {
    private Long friendshipId;
    private Long requesterId;
    private String requesterUsername;
    private String imageUrl;
}
