package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.group.GroupDTO;
import com.example.SpringSecurity.model.ChatGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupDTO convertToGroupDTO(ChatGroup groupDTO);
}
