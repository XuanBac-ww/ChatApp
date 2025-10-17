package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-28T22:10:25+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO convertToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setFullName( user.getFullName() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setRole( user.getRole() );

        return userDTO;
    }
}
