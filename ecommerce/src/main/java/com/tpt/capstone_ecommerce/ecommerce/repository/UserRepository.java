package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@Email(message = "Email is invalid") @Size(max = 255, message = "Email is too long") String email);

    boolean existsByEmail(@Email(message = "Email is invalid") @Size(max = 255, message = "Email is too long") String email);
}
