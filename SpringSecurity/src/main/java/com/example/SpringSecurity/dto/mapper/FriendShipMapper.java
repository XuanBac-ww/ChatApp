package com.example.SpringSecurity.dto.mapper;

import com.example.SpringSecurity.dto.response.friend.FriendShipResponseDTO;
import com.example.SpringSecurity.model.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FriendShipMapper {

    @Mapping(source = "id", target = "friendshipId")
    @Mapping(source = "friendshipStatus", target = "status")
    @Mapping(source = "requester", target = "requester")
    @Mapping(source = "addressee", target = "addressee")
    FriendShipResponseDTO toResponseDTO(Friendship friendship);

}