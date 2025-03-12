package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.dto.jpa.SkuMinPriceDTO;
import com.tpt.capstone_ecommerce.ecommerce.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, String> {

    @Query(value = "SELECT s.discount, s.price FROM skus s WHERE s.spu_id = ?1 AND s.status = 'ACTIVE' ORDER BY s.price ASC LIMIT 1", nativeQuery = true)
    Optional<SkuMinPriceDTO> findBySpuIdWithMinPrice(String spuId);

    @Query(value = "SELECT * FROM skus WHERE spu_id = ?1 AND status = ?2", nativeQuery = true)
    List<Sku> findAllActiveBySpuId(String spuId, String status);

    @Query(value = "SELECT * FROM skus WHERE spu_id = ?1", nativeQuery = true)
    List<Sku> findAllBySpuId(String spuId);
}
