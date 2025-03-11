package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query(value = "SELECT * FROM categories WHERE id = ?1", nativeQuery = true)
    Optional<Category> findById(String id);

    @Query(value = "SELECT * FROM categories WHERE name = ?1", nativeQuery = true)
    Optional<Category> findByName(String name);
}
