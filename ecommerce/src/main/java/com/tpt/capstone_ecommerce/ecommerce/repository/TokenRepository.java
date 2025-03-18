package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    @Query(value = "SELECT * FROM tokens WHERE refresh_token = ?1", nativeQuery = true)
    Optional<Token> findByRefreshToken(String refreshToken);
}
