package com.example.SpringSecurity.dto.request.friend;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendRequest {
    @NotNull
    private Long userId;
}
