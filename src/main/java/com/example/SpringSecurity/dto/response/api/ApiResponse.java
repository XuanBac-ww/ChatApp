package com.example.SpringSecurity.dto.response.api;

import com.example.SpringSecurity.dto.response.api.abstraction.BaseResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> extends BaseResponse {
    private T data;

    public ApiResponse(int status,boolean success,String message,T data) {
        super(status,success,message);
        this.data = data;
    }
}
