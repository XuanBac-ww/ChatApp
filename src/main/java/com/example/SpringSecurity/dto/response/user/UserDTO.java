package com.example.SpringSecurity.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.util.Date;

@Data
public class UserDTO {

    private String fullName;
    private String email;
    private String numberPhone;
    private String profileImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdAt;
}
