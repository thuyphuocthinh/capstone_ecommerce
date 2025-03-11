package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, String> {

    @Query(value = "SELECT * FROM shops WHERE owner_id = ?1", nativeQuery = true)
    Optional<Shop> findByOwnerId(String ownerId);
}
