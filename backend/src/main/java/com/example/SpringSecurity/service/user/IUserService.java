package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.dto.request.user.UserUpdateRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.dto.response.user.UserSearchDTO;
import com.example.SpringSecurity.model.User;
import org.springframework.transaction.annotation.Transactional;


public interface IUserService {
    ApiResponse<UserDTO> getUserInfo(Long userId);

    ApiResponse<UserDTO> updateUser(UserUpdateRequest userUpdateRequest, Long userId);

    PageResponse<UserDTO> getAllUser(int page, int size);

    ApiResponse<UserDTO> findUserByFullName(String fullName);

    ApiResponse<?> deleteUser(Long userId);

    PageResponse<UserDTO> getDeletedUsers(int page, int size);

    ApiResponse<UserSearchDTO> searchUsers(String phone, Long userId);
}
