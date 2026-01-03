package com.example.SpringSecurity.service.user;

import com.example.SpringSecurity.dto.mapper.UserMapper;
import com.example.SpringSecurity.dto.request.user.UserUpdateRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.user.UserDTO;
import com.example.SpringSecurity.dto.response.user.UserSearchDTO;
import com.example.SpringSecurity.enums.FriendshipStatus;
import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IFriendshipRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserMapper userMapper;
    private final IUserRepository userRepository;
    private final IFriendshipRepository friendshipRepository;

    @Override
    @Cacheable(value = "user_info", key = "#userId")
    public ApiResponse<UserDTO> getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));

        UserDTO userDTO = userMapper.convertToUserDTO(user);

        return new ApiResponse<>(200, true, "Get information successfully", userDTO);
    }

    @Override
    @Caching(
            put = { @CachePut(value = "user_info", key = "#userId") },
            evict = { @CacheEvict(value = "users_list", allEntries = true) }
    )
    @Transactional
    public ApiResponse<UserDTO> updateUser(UserUpdateRequest userUpdateRequest, Long userId) {
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
    @Cacheable(value = "users_list", key = "'active_' + #page + '_' + #size")
    public PageResponse<UserDTO> getAllUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findActiveByRole(Role.ROLE_USER, pageable);
        List<UserDTO> userDTO = userPage.getContent()
                .stream()
                .map(userMapper::convertToUserDTO)
                .toList();
        return new PageResponse<>(
                200,
                true,
                "Get All user Info successfully",
                userDTO,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast());
    }

    @Override
    @Cacheable(value = "user_fullname", key = "#fullName")
    public ApiResponse<UserDTO> findUserByFullName(String fullName) {
        User user = userRepository.findByFullName(fullName)
                .orElseThrow(() -> new AppException("User not found with fullName: " + fullName));
        UserDTO userDTO = userMapper.convertToUserDTO(user);
        return new ApiResponse<>(200, true, "Find user by fullName successfully", userDTO);
    }

    //Xoa mem entity
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user_info", key = "#userId"),       // Xóa cache chi tiết user
            @CacheEvict(value = "users_list", allEntries = true)     // Xóa toàn bộ cache danh sách
    })
    public ApiResponse<?> deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findActiveById(userId);
        if (userOptional.isEmpty()) {
            return new ApiResponse<>(200, false, "User not found or already deleted", null);
        }
        userRepository.softDelete(userId);
        return new ApiResponse<>(200, true, "User deleted successfully", null);
    }

    // Tim tat ca entity da bi xoa mem
    @Override
    @Cacheable(value = "users_list", key = "'deleted_' + #page + '_' + #size")
    public PageResponse<UserDTO> getDeletedUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> deletedUserPage = userRepository.findAllInactive(pageable);

        List<UserDTO> userDTOs = deletedUserPage.getContent()
                .stream()
                .map(userMapper::convertToUserDTO)
                .toList();
        return new PageResponse<>(
                200,
                true,
                "Retrieved deleted users successfully",
                userDTOs,
                deletedUserPage.getNumber(),
                deletedUserPage.getSize(),
                deletedUserPage.getTotalElements(),
                deletedUserPage.getTotalPages(),
                deletedUserPage.isLast());
    }

    @Override
    public ApiResponse<UserSearchDTO> searchUsers(String phone, Long currentUserId) {
        User targetUser = userRepository.findByNumberPhoneAndIdNot(phone, currentUserId)
                .orElse(null);

        if (targetUser == null) {
            return new ApiResponse<>(200, false, "User Not Found with Number Phone", null);
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException("User current Not Found"));

        UserSearchDTO userDTO = userMapper.mapToUserSearchDTO(targetUser);

        Optional<Friendship> friendship = friendshipRepository.findFriendshipBetweenUsers(currentUser, targetUser);
        String status = determineFriendshipStatus(friendship, currentUserId);

        userDTO.setStatus(status);

        return new ApiResponse<>(200, true, "User Found", userDTO);
    }



    private String determineFriendshipStatus(Optional<Friendship> friendshipOptional, Long currentUserId) {
        if (friendshipOptional.isEmpty()) {
            return "NONE";
        }

        Friendship fs = friendshipOptional.get();
        FriendshipStatus status = fs.getFriendshipStatus();

        return switch (status) {
            case ACCEPTED -> "FRIEND";
            case BLOCKED -> "BLOCKED";
            case PENDING -> {
                boolean isRequester = fs.getRequester().getId().equals(currentUserId);
                yield isRequester ? "PENDING_SENT" : "PENDING_RECEIVED";
            }
            default -> "NONE";
        };
    }
}

