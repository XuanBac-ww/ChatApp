package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.dto.mapper.UserMapper;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<UserDTO> getUserInfo(Long userId) {
        log.info("Get info userId starting {} ", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));

        log.info("Get info userId starting {} {} ", user.getFullName(),user.getEmail());

        UserDTO userDTO = userMapper.convertToUserDTo(user);
        log.info("Get info userId starting {} {} ", userDTO.getFullName(),userDTO.getEmail());
        return new ApiResponse<>(200,true,"Get Info successfully",userDTO);
    }
}
