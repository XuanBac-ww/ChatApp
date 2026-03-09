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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
        log.info("Start send friend request from userId={} to userId={}", senderId, request.getUserId());
        User sender = userValidationService.findById(senderId);
        User receiver = userValidationService.findById(request.getUserId());

        validateNotSelfRequest(senderId, receiver.getId());

        friendshipRepository.findFriendshipBetweenUsers(sender, receiver).ifPresent(fs -> {
            log.error("Friend request already exists between senderId={} and receiverId={}", senderId, receiver.getId());
            throw new AppException("Your already sent a request or you are already friends");
        });

        Friendship newRequest = Friendship.builder()
                .requester(sender)
                .addressee(receiver)
                .friendshipStatus(FriendshipStatus.PENDING)
                .build();

        Friendship savedFriendship = friendshipRepository.save(newRequest);
        FriendShipResponseDTO responseDTO = friendShipMapper.toResponseDTO(savedFriendship);
        log.info("Send friend request success from userId={} to userId={}", senderId, request.getUserId());
        return new ApiResponse<>(200, true, "Send request successfully", responseDTO);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user_friends", allEntries = true),
            @CacheEvict(value = "pending_requests", allEntries = true)
    })
    public ApiResponse<?> acceptFriendRequest(Long userId, Long requestId) {
        log.info("Start accept friend request receiverId={} requesterId={}", userId, requestId);
        User currentUser = userValidationService.findById(userId);
        User requester = userValidationService.findById(requestId);

        Friendship friendship = findPendingRequestOrThrow(currentUser, requester);

        friendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        log.info("Accept friend request success receiverId={} requesterId={}", userId, requestId);
        return new ApiResponse<>(200, true, "Accept request successfully", null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "pending_requests", allEntries = true)
    public ApiResponse<?> rejectFriendRequest(Long currentId, Long requestId) {
        log.info("Start reject friend request receiverId={} requesterId={}", currentId, requestId);
        User currentUser = userValidationService.findById(currentId);
        User requester = userValidationService.findById(requestId);

        Friendship friendship = findPendingRequestOrThrow(currentUser, requester);

        friendshipRepository.delete(friendship);
        log.info("Reject friend request success receiverId={} requesterId={}", currentId, requestId);
        return new ApiResponse<>(200, true, "Reject request successfully", null);
    }

    @Override
    @Cacheable(value = "user_friends", key = "#userId + '_' + #page + '_' + #size")
    public PageResponse<?> getAllFriend(Long userId, int page, int size) {
        log.info("Start get all friends for userId={} page={} size={}", userId, page, size);
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
        log.info("Get all friends success for userId={}", userId);

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
        log.info("Start get pending requests for userId={} page={} size={}", userId, page, size);
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
        log.info("Get pending requests success for userId={}", userId);

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

    private void validateNotSelfRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new AppException("Not send request to yourself");
        }
    }

    private Friendship findPendingRequestOrThrow(User currentUser, User requester) {
        return friendshipRepository.findFriendshipBetweenUsers(currentUser, requester)
                .filter(fs -> fs.getFriendshipStatus() == FriendshipStatus.PENDING && fs.getAddressee().equals(currentUser))
                .orElseThrow(() -> new AppException("Request Not Found"));
    }
}