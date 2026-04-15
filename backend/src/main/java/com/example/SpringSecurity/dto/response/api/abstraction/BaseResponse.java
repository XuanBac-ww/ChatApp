package com.example.SpringSecurity.dto.response.api.abstraction;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseResponse {
    private int status;
    private boolean success;
    private String message;
}
