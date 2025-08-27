package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.dto.request.user.UserUpdateRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import org.springframework.transaction.annotation.Transactional;

public interface IUserService {
    ApiResponse<UserDTO> getUserInfo(Long userId);

    @Transactional
    ApiResponse<UserDTO> updateUser(UserUpdateRequest userUpdateRequest, Long userId);

    PageResponse<UserDTO> getAllUser(int page, int size);

    @Transactional
    ApiResponse<?> deleteUser(Long userId);


    PageResponse<UserDTO> getDeletedUsers(int page, int size);
}
