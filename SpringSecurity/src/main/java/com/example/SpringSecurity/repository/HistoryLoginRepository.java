package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.HistoryLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryLoginRepository extends JpaRepository<HistoryLogin,Long> {

    Optional<HistoryLogin> findByToken(String token);

    Optional<HistoryLogin> findByUserId(Long userId);
}
