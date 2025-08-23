package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO convertToUserDTo(User user);
}
