package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.HistoryLogin;
import com.example.SpringSecurity.repository.Abstraction.ISoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IHistoryLoginRepository extends ISoftDeleteRepository<HistoryLogin,Long> {

    Optional<HistoryLogin> findByToken(String token);

    Optional<HistoryLogin> findByUserId(Long userId);
}
