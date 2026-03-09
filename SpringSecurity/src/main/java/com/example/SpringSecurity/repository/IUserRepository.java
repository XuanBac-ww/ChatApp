package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.enums.Role;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.ISoftDeleteRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends ISoftDeleteRepository<User,Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.role = :role")
    Page<User> findActiveByRole(@Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = true")
    Optional<User> findByEmailIncludeDeleted(@Param("email") String email);

    Optional<User> findByNumberPhoneAndIdNot(String phoneNumber, Long id);

    Optional<User> findByFullName(String fullName);
}
