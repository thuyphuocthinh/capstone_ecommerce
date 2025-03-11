package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
    @Query(value = "SELECT * FROM brands WHERE id = ?1", nativeQuery = true)
    Optional<Brand> findById(String id);

    @Query(value = "SELECT * FROM brands WHERE name = ?1", nativeQuery = true)
    Optional<Brand> findByBrandName(String brandName);
}
