package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.ISoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends ISoftDeleteRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);
}
