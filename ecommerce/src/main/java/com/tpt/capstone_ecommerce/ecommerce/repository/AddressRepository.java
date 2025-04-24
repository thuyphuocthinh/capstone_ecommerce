package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Address;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    @Query(
            value = "SELECT * FROM addresses WHERE user_id = ?1",
            countQuery = "SELECT COUNT(*) FROM addresses WHERE user_id = ?1",
            nativeQuery = true
    )
    Page<Address> findAllByUser(String userId, Pageable pageable);

    @Query(value = "SELECT * FROM addresses WHERE phone = :phone LIMIT 1", nativeQuery = true)
    Optional<Address> findByPhone(@Param("phone") String phone);
}
