package com.example.SpringSecurity.dto.response.user;

import com.example.SpringSecurity.enums.Role;
import lombok.*;

@Data
public class UserDTO {

    private String fullName;
    private String email;
    private String numberPhone;
    private Role role;

}
