package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.model.User;

import java.util.List;

public interface IUserService {
    ApiResponse<UserDTO> getUserInfo(Long userId);

    ApiResponse<List<UserDTO>>getAllUser();
}
