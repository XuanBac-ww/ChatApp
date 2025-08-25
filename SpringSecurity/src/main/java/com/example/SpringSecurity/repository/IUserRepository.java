package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.custom.user.IUserRepositoryCustom;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.Abstraction.ISoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends ISoftDeleteRepository<User,Long>, IUserRepositoryCustom {
    Optional<User> findByEmail(String username);
}
