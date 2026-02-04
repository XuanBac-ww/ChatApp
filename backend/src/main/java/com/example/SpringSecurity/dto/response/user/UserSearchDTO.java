package com.example.SpringSecurity.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchDTO {
    private Long userId;
    private String fullName;
    private String avatar;
    private String status;
}
