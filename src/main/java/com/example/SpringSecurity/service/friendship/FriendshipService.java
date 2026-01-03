package com.example.SpringSecurity.service.friendship;

import com.example.SpringSecurity.dto.mapper.FriendShipMapper;
import com.example.SpringSecurity.dto.request.friend.FriendRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.friend.FriendDTO;
import com.example.SpringSecurity.dto.response.friend.FriendRequestResponse;
import com.example.SpringSecurity.dto.response.friend.FriendShipResponseDTO;
import com.example.SpringSecurity.enums.FriendshipStatus;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IFriendshipRepository;
import com.example.SpringSecurity.service.user.IUserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipService implements IFriendshipService {

    private final IUserValidationService userValidationService;
    private final IFriendshipRepository friendshipRepository;
    private final FriendShipMapper friendShipMapper;

    @Override
    @Transactional
    @CacheEvict(value = "pending_requests", allEntries = true)
    public ApiResponse<FriendShipResponseDTO> sendAddFriend(Long senderId, FriendRequest request) {
        User sender = userValidationService.findById(senderId);
        User receiver = userValidationService.findById(request.getUserId());

        if (senderId.equals(receiver.getId())) {
            return new ApiResponse<>(200, false, "Not send request to yourself", null);
        }

        friendshipRepository.findFriendshipBetweenUsers(sender, receiver).ifPresent(fs -> {
            throw new AppException("Your already sent a request or you are already friends");
        });

        Friendship newRequest = Friendship.builder()
                .requester(sender)
                .addressee(receiver)
                .friendshipStatus(FriendshipStatus.PENDING)
                .build();

        Friendship savedFriendship = friendshipRepository.save(newRequest);
        FriendShipResponseDTO responseDTO = friendShipMapper.toResponseDTO(savedFriendship);
        return new ApiResponse<>(200, true, "Send request successfully", responseDTO);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user_friends", allEntries = true),
            @CacheEvict(value = "pending_requests", allEntries = true)
    })
    public ApiResponse<?> acceptFriendRequest(Long userId, Long requestId) {
        User currentUser = userValidationService.findById(userId);
        User requester = userValidationService.findById(requestId);

        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(currentUser, requester)
                .filter(fs -> fs.getFriendshipStatus() == FriendshipStatus.PENDING && fs.getAddressee().equals(currentUser))
                .orElseThrow(() -> new AppException("Request Not Found"));

        friendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        return new ApiResponse<>(200, true, "Accept request successfully", null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "pending_requests", allEntries = true)
    public ApiResponse<?> rejectFriendRequest(Long currentId, Long requestId) {
        User currentUser = userValidationService.findById(currentId);
        User requester = userValidationService.findById(requestId);

        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(currentUser, requester)
                .filter(fs -> fs.getFriendshipStatus() == FriendshipStatus.PENDING && fs.getAddressee().equals(currentUser))
                .orElseThrow(() -> new AppException("Request Not Found"));

        friendshipRepository.delete(friendship);
        return new ApiResponse<>(200, true, "Reject request successfully", null);
    }

    @Override
    @Cacheable(value = "user_friends", key = "#userId + '_' + #page + '_' + #size")
    public PageResponse<?> getAllFriend(Long userId, int page, int size) {
        User currentUser = userValidationService.findById(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<Friendship> pageOfFriendships = friendshipRepository.findAllFriendsByUser(
                currentUser,
                FriendshipStatus.ACCEPTED,
                pageable
        );

        List<FriendDTO> friends = pageOfFriendships.getContent().stream()
                .map(friendship -> {
                    // Xác định ai là bạn
                    User friendEntity = friendship.getRequester().getId().equals(currentUser.getId())
                            ? friendship.getAddressee()
                            : friendship.getRequester();

                    String imageUrl = (friendEntity.getProfileImage() != null)
                            ? friendEntity.getProfileImage().getUrl()
                            : null;

                    return new FriendDTO(
                            friendEntity.getId(),
                            friendEntity.getFullName(),
                            imageUrl
                    );
                })
                .toList();

        return new PageResponse<>(
                200, true, "Get all friends successfully",
                friends,
                pageOfFriendships.getNumber(),
                pageOfFriendships.getSize(),
                pageOfFriendships.getTotalElements(),
                pageOfFriendships.getTotalPages(),
                pageOfFriendships.isLast()
        );
    }

    @Override
    @Cacheable(value = "pending_requests", key = "#userId + '_' + #page + '_' + #size")
    public PageResponse<FriendRequestResponse> getPendingFriendRequest(Long userId, int page, int size) {
        User currentUser = userValidationService.findById(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<Friendship> pageOfRequests = friendshipRepository.findByAddresseeAndFriendshipStatus(
                currentUser,
                FriendshipStatus.PENDING,
                pageable
        );

        List<FriendRequestResponse> requestDtos = pageOfRequests.getContent().stream()
                .map(friendship -> {
                    User requester = friendship.getRequester();
                    // Lấy ảnh trực tiếp từ User entity
                    String imageUrl = (requester.getProfileImage() != null)
                            ? requester.getProfileImage().getUrl()
                            : null;

                    return new FriendRequestResponse(
                            friendship.getId(),
                            requester.getId(),
                            requester.getFullName(),
                            imageUrl
                    );
                })
                .toList();

        return new PageResponse<>(
                200, true, "Get pending friend request successfully",
                requestDtos,
                pageOfRequests.getNumber(),
                pageOfRequests.getSize(),
                pageOfRequests.getTotalElements(),
                pageOfRequests.getTotalPages(),
                pageOfRequests.isLast()
        );
    }
}