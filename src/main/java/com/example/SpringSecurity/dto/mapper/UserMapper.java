package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.dto.response.user.UserSearchDTO;
import com.example.SpringSecurity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "profileImage.url", target = "profileImage")
    UserDTO convertToUserDTO(User user);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "profileImage.url", target = "avatar")
    UserSearchDTO mapToUserSearchDTO(User user);
}
