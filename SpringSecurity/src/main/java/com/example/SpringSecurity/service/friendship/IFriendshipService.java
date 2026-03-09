package com.example.SpringSecurity.service.friendship;

import com.example.SpringSecurity.dto.request.friend.FriendRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.friend.FriendShipResponseDTO;


public interface IFriendshipService {

    ApiResponse<FriendShipResponseDTO> sendAddFriend(Long senderId, FriendRequest request);

    ApiResponse<?> acceptFriendRequest(Long userId, Long requestId);

    ApiResponse<?> rejectFriendRequest(Long userId, Long senderId);

    PageResponse<?> getAllFriend(Long userId, int page, int size);

    PageResponse<?> getPendingFriendRequest(Long userId, int page, int size);
}
