package com.example.SpringSecurity.repository.Abstraction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ISoftDeleteRepository<T,ID> extends JpaRepository<T,ID> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    List<T> findAllActive(); // Tim tat ca entity chua bi xoa mem

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    Page<T> findAllInactive(Pageable pageable);      // tim entity da bi xoa mem

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false AND e.id = ?1")
    Optional<T> findActiveById(ID id);       // Tim entity chua bi xoa mem

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deletedAt = CURRENT_TIMESTAMP WHERE e.id = ?1")
    void softDelete(ID id);     // xoa mem entity dat delete is true

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = false, e.deletedAt = null WHERE e.id = ?1")
    void restore(ID id);    // khoi phuc lai entity
}
