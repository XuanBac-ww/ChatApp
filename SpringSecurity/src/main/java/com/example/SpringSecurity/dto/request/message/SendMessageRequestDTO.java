package com.example.SpringSecurity.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequestDTO {

    @NotBlank(message = "Tin nhan khong duoc de trong")
    private String content;
}
