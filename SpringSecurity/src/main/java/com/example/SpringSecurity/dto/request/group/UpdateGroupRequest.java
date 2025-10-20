package com.example.SpringSecurity.dto.request.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateGroupRequest {
    private Long groupId;

    @NotBlank
    @Size(max = 30)
    private String groupName;
}
