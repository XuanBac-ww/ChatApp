package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.dto.mapper.UserMapper;
import com.example.SpringSecurity.dto.request.user.UserUpdateRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final IUserRepository userRepository;

    @Override
    @Cacheable(value = "user",key = "#userId")
    public ApiResponse<UserDTO> getUserInfo(Long userId) {
        log.info("Get info userId is {} starting ", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
        log.info("Get info userId starting {} {} ", user.getFullName(), user.getEmail());
        UserDTO userDTO = userMapper.convertToUserDTO(user);
        log.info("Get info userId is {} {} starting ", userDTO.getFullName(), userDTO.getEmail());
        return new ApiResponse<>(200, true, "Get Info successfully", userDTO);
    }

    @Transactional
    @Override
    @CachePut(value = "user",key = "#userId")
    public ApiResponse<UserDTO> updateUser(UserUpdateRequest userUpdateRequest, Long userId) {
        log.info("Update Info UserId is {}", userId);
        User user = userRepository.findById(userId)
                .map(u -> {
                    u.setFullName(userUpdateRequest.getFullName());
                    u.setNumberPhone(userUpdateRequest.getNumberPhone());
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new AppException("User not found with id: " + userId));
        UserDTO userDTO = userMapper.convertToUserDTO(user);
        return new ApiResponse<>(200, true, "Update Successfully", userDTO);
    }


    // Tim tat ca entity chua bi xoa mem
    @Override
    @Cacheable(value = "userAll",key = "#user")
    public PageResponse<UserDTO> getAllUser(int page, int size) {
        log.info("Get info all user is starting ");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findActiveByRole(Role.ROLE_USER, pageable);
        List<UserDTO> userDTO = userPage.getContent()
                .stream()
                .map(userMapper::convertToUserDTO)
                .toList();
        return new PageResponse<>(200, true, "Get All user Info successfully", userDTO,
                userPage.getNumber(), userPage.getSize(), userPage.getTotalElements(),
                userPage.getTotalPages(), userPage.isLast());
    }


    //Xoa mem entity
    @Transactional
    @Override
    public ApiResponse<?> deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findActiveById(userId);
        if (userOptional.isEmpty()) {
            return new ApiResponse<>(200, true, "User not found or already deleted", null);
        }
        userRepository.softDelete(userId);
        return new ApiResponse<>(200, true, "User deleted successfully", null);
    }

    // Tim tat ca entity da bi xoa mem
    @Override
    public PageResponse<UserDTO> getDeletedUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> deletedUserPage = userRepository.findAllInactive(pageable);

        List<UserDTO> userDTOs = deletedUserPage.getContent()
                .stream()
                .map(userMapper::convertToUserDTO)
                .toList();
        return new PageResponse<>(200, true, "Retrieved deleted users successfully",
                userDTOs, deletedUserPage.getNumber(), deletedUserPage.getSize(), deletedUserPage.getTotalElements(),
                deletedUserPage.getTotalPages(), deletedUserPage.isLast());
    }
}

