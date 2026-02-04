package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Image;
import com.example.SpringSecurity.repository.Abstraction.ISoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IImageRepository extends ISoftDeleteRepository<Image,Long> {
    Optional<Image> findByHash(String newHash);
}
