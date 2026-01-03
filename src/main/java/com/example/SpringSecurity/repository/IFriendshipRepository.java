package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.enums.FriendshipStatus;
import com.example.SpringSecurity.model.Friendship;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFriendshipRepository extends IBaseRepository<Friendship, Long> {
    // Tìm kiếm mối quan hệ giữa 2 user, bất kể ai là người gửi
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester = :currentUser AND f.addressee = :userToAdd) OR " +
            "(f.requester = :userToAdd AND f.addressee = :currentUser)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("currentUser") User currentUser, @Param("userToAdd") User userToAdd);

    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester = :user OR f.addressee = :user) AND f.friendshipStatus = :status")
    Page<Friendship> findAllFriendsByUser(@Param("user") User user, @Param("status") FriendshipStatus status, Pageable pageable);

    Page<Friendship> findByAddresseeAndFriendshipStatus(User addressee, FriendshipStatus status, Pageable pageable);

}