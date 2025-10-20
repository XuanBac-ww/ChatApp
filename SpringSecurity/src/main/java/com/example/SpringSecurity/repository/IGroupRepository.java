package com.example.SpringSecurity.repository;

import com.example.SpringSecurity.model.Group;
import com.example.SpringSecurity.repository.Abstraction.IBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGroupRepository extends IBaseRepository<Group,Long> {
}
