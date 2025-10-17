package com.example.SpringSecurity.service.friendship;


import com.example.SpringSecurity.dto.request.user.NumberPhoneRequest;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.dto.response.api.PageResponse;
import com.example.SpringSecurity.enums.FriendshipStatus;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IFriendshipRepository;
import com.example.SpringSecurity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {
    private final IUserRepository userRepository;
    private final IFriendshipRepository friendshipRepository;
    @Override
    public ApiResponse<?> sendAddFriend(Long senderId, NumberPhoneRequest numberPhoneRequest) {
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

        // Tìm và xóa lời mời
        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(currentUser, requester)
                .filter(fs -> fs.getFriendshipStatus() == FriendshipStatus.PENDING && fs.getAddressee().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Khong Tim Thay Loi Moi Hoac Ban Khong phai la Nguoi nhan"));

        friendshipRepository.delete(friendship);
        return new ApiResponse<>(200,true,"Tu choi ket ban Thanh Cong",null);
    }

    @Override
    public PageResponse<?> getAllFriend(Long userId, int page, int size) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> pageOfFriendships = friendshipRepository.findAllFriendsByUser(
                currentUser,
                FriendshipStatus.ACCEPTED,
                pageable
        );

        List<User> friends = pageOfFriendships.getContent().stream()
                .map(friendship -> {
                    if(friendship.getRequester().getId().equals(currentUser.getId())) {
                        return friendship.getAddressee();
                    } else {
                        return friendship.getRequester();
                    }
                })
                .toList();
        return new PageResponse<>(
                200, // status
                true, // success
                "Lay Tat ca ban be thanh cong",
                friends, // data
                pageOfFriendships.getNumber(),
                pageOfFriendships.getSize(),
                pageOfFriendships.getTotalElements(),
                pageOfFriendships.getTotalPages(),
                pageOfFriendships.isLast()
        );
    }

    @Override
    public PageResponse<?> getPendingFriendRequest(Long userId, int page, int size) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User Not Found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> pageOfRequests = friendshipRepository.findByAddresseeAndFriendshipStatus(
                currentUser,
                FriendshipStatus.PENDING,
                pageable
        );

        List<User> requesters = pageOfRequests.getContent().stream()
                .map(Friendship::getRequester)
                .toList();
        return new PageResponse<>(
                200,
                true,
                "Tat ca Loi Moi Ket Ban Thanh Cong",
                requesters,
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
