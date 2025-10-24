package com.example.SpringSecurity.service.friendship;


import com.example.SpringSecurity.dto.request.user.NumberPhoneRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.dto.response.friend.FriendDTO;
import com.example.SpringSecurity.dto.response.friend.FriendRequestResponse;
import com.example.SpringSecurity.enums.FriendshipStatus;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IFriendshipRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {
    private final IUserRepository userRepository;
    private final IFriendshipRepository friendshipRepository;
    @Override
    public ApiResponse<Friendship> sendAddFriend(Long senderId, NumberPhoneRequest numberPhoneRequest) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException("User Not Found"));
        User receiver = findUserByNumberPhone(numberPhoneRequest.getNumberPhone());
        if(senderId.equals(receiver.getId())) {
            return new ApiResponse<>(200,false,"Khong duoc gui ket ban chinh minh",null);
        }

        friendshipRepository.findFriendshipBetweenUsers(sender,receiver).ifPresent(
                fs -> {
                    throw new AppException("Loi gui ket ban Da duoc gui hoac cac ban da la ban be");
                }
        );

        Friendship newRequest = Friendship.builder()
                .requester(sender)
                .addressee(receiver)
                .friendshipStatus(FriendshipStatus.PENDING)
                .build();
        friendshipRepository.save(newRequest);
        return new ApiResponse<>(200,true,"Gui yeu cau ket ban thanh cong",newRequest);
    }

    @Override
    public ApiResponse<?> acceptFriendRequest(Long userId, Long requestId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
        User requester = userRepository.findById(requestId)
                .orElseThrow(() -> new AppException("Requester Not Found"));
        // Tìm lời mời mà currentUser là người nhận
        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(currentUser, requester)
                .filter(fs -> fs.getFriendshipStatus() == FriendshipStatus.PENDING && fs.getAddressee().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Friend request not found or you are not the recipient."));

        friendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        return new ApiResponse<>(200,true,"Chap Nhan Thanh Cong Loi Moi Ket Ban",null);
    }

    @Override
    public ApiResponse<?> rejectFriendRequest(Long currentId, Long requestId) {
        User currentUser = userRepository.findById(currentId)
                .orElseThrow(() -> new AppException("User Not Found"));
        User requester = userRepository.findById(requestId)
                .orElseThrow(() -> new AppException("Requester Not Found"));


        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(currentUser, requester)
                .filter(fs -> fs.getFriendshipStatus() == FriendshipStatus.PENDING && fs.getAddressee().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Khong Tim Thay Loi Moi Hoac Ban Khong phai la Nguoi nhan"));

        friendshipRepository.delete(friendship);
        return new ApiResponse<>(200,true,"Tu choi ket ban Thanh Cong",null);
    }

    @Override
    @Cacheable(value = "AllFriend",key = "#userId")
    public PageResponse<?> getAllFriend(Long userId, int page, int size) {
        log.info("");
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> pageOfFriendships = friendshipRepository.findAllFriendsByUser(
                currentUser,
                FriendshipStatus.ACCEPTED,
                pageable
        );

        List<FriendDTO> friends = pageOfFriendships.getContent().stream()
                .map(friendship -> {
                    User friendEntity;
                    if(friendship.getRequester().getId().equals(currentUser.getId())) {
                        friendEntity = friendship.getAddressee();
                    } else {
                        friendEntity = friendship.getRequester();
                    }
                    return new FriendDTO(
                            friendEntity.getId(),
                            friendEntity.getFullName()
                    );
                })
                .toList();
        return new PageResponse<>(
                200,
                true,
                "Lay Tat ca ban be thanh cong",
                friends,
                pageOfFriendships.getNumber(),
                pageOfFriendships.getSize(),
                pageOfFriendships.getTotalElements(),
                pageOfFriendships.getTotalPages(),
                pageOfFriendships.isLast()
        );
    }

    @Override
    @Cacheable(value = "AllRequest",key = "#userId")
    public PageResponse<?> getPendingFriendRequest(Long userId, int page, int size) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> pageOfRequests = friendshipRepository.findByAddresseeAndFriendshipStatus(
                currentUser,
                FriendshipStatus.PENDING,
                pageable
        );

        List<FriendRequestResponse> requestDtos = pageOfRequests.getContent().stream()
                .map(friendship -> {
                    User requester = friendship.getRequester();
                    return new FriendRequestResponse(
                            friendship.getId(),
                            requester.getId(),
                            requester.getFullName()
                    );
                })
                .toList();
        return new PageResponse<>(
                200,
                true,
                "Tat ca Loi Moi Ket Ban Thanh Cong",
                requestDtos,
                pageOfRequests.getNumber(),
                pageOfRequests.getSize(),
                pageOfRequests.getTotalElements(),
                pageOfRequests.getTotalPages(),
                pageOfRequests.isLast()
        );
    }

    private User findUserByNumberPhone(String phoneNumber) {
        return userRepository.findByNumberPhone(phoneNumber)
                .orElseThrow(() -> new AppException("User Not Found"));
    }
}
