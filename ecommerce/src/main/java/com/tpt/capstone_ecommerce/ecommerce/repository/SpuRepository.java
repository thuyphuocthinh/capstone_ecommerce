package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Spu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpuRepository extends JpaRepository<Spu, String> {

    @Query(value = "SELECT * FROM spus WHERE status = 'ACTIVE'", nativeQuery = true)
    Page<Spu> findAllByActiveStatus(Pageable pageable);

    @Query(value = "SELECT * FROM spus WHERE status != 'DELETED'", nativeQuery = true)
    Page<Spu> findAllByUndeletedStatus(Pageable pageable);
}
