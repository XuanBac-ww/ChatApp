package com.example.SpringSecurity.dto.response.groupMember;

import com.example.SpringSecurity.enums.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDTO {
    private Long userId;
    private String fullName;
    private GroupRole role;
    private Instant createdAt;
}
