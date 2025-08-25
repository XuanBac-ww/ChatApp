package com.example.SpringSecurity.custom.user;

import com.example.SpringSecurity.dto.request.auth.RegisterUserRequest;
import com.example.SpringSecurity.model.User;

public interface IUserRepositoryCustom {
    User createUser(RegisterUserRequest userRequest);

}
