package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.model.VerifyOTP;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVerifyOTPRepository extends IBaseRepository< VerifyOTP,Long> {
    Optional<VerifyOTP> findByUser(User user);
}
