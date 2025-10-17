package com.example.SpringSecurity.service.friendship;

import com.example.SpringSecurity.dto.request.user.NumberPhoneRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import org.springframework.data.domain.Pageable;

public interface IFriendshipService {

    ApiResponse<?> sendAddFriend(Long senderId, NumberPhoneRequest numberPhoneRequest);

    ApiResponse<?> acceptFriendRequest(Long userId, Long requestId);

    ApiResponse<?> rejectFriendRequest(Long userId, Long senderId);

    PageResponse<?> getAllFriend(Long userId, int page, int size);

    PageResponse<?> getPendingFriendRequest(Long userId, int page, int size);
}
