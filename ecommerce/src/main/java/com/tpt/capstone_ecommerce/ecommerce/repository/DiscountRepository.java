package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Discount;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    Discount findByCode(@NotBlank(message = "Discount code cannot be blank") @Size(min = 1, max = 50, message = "Discount code length is invalid") String code);
    Page<Discount> findAllByCreator(User user, Pageable pageable);
}
