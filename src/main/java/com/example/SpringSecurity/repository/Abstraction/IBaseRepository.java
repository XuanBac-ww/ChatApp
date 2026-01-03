package com.example.SpringSecurity.repository.Abstraction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IBaseRepository<T,ID> extends JpaRepository<T,ID> {
}
