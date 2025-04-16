package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.dto.response.SpuDetailResponse;
import com.tpt.capstone_ecommerce.ecommerce.entity.Spu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpuRepository extends JpaRepository<Spu, String> {

    @Query(value = "SELECT * FROM spus WHERE status = 'ACTIVE'", nativeQuery = true)
    Page<Spu> findAllByActiveStatus(Pageable pageable);

    @Query(value = "SELECT * FROM spus WHERE status != 'DELETED'", nativeQuery = true)
    Page<Spu> findAllByUndeletedStatus(Pageable pageable);

    @Query(value = "SELECT * FROM spus WHERE shop_id = ?1", nativeQuery = true)
    Page<Spu> findAllByShopId(String shopId, Pageable pageable);

    @Query(value = "SELECT * FROM spus WHERE brand_id = ?1", nativeQuery = true)
    Page<Spu> findAllByBrandId(String brandId, Pageable pageable);

    @Query(value = "SELECT * FROM spus WHERE category_id = ?1", nativeQuery = true)
    Page<Spu> findAllByCategoryId(String categoryId, Pageable pageable);

    @Query(value = """
        SELECT sp.id, sp.name, sp.description, sp.image_url, sp.slug,
               sp.brand_id, b.name AS brand_name, sp.category_id, c.name AS category_name,
               sp.shop_id, MIN(sk.price) AS price, sk.discount
        FROM spus sp
        JOIN skus sk ON sp.id = sk.spu_id
        JOIN brands b ON sp.brand_id = b.id
        JOIN categories c ON sp.category_id = c.id
        WHERE (:brandIds IS NULL OR sp.brand_id IN (:brandIds))
        AND (:categoryIds IS NULL OR sp.category_id IN (:categoryIds))
        AND (:name IS NULL OR MATCH(sp.name) AGAINST (CONCAT('+', :name, '*') IN BOOLEAN MODE))
        GROUP BY sp.id, sp.name, sp.description, sp.image_url, sp.slug,
                 sp.brand_id, b.name, sp.category_id, c.name, sp.shop_id, sk.discount
        ORDER BY price ASC
    """, nativeQuery = true)
    Page<SpuDetailResponse> findByBrandAndCategorySortedAsc(
            @Param("name") String name,
            @Param("brandIds") List<String> brandIds,
            @Param("categoryIds") List<String> categoryIds,
            Pageable pageable);

    @Query(value = """
        SELECT sp.id, sp.name, sp.description, sp.image_url, sp.slug,
               sp.brand_id, b.name AS brand_name, sp.category_id, c.name AS category_name,
               sp.shop_id, MIN(sk.price) AS price, sk.discount
        FROM spus sp
        JOIN skus sk ON sp.id = sk.spu_id
        JOIN brands b ON sp.brand_id = b.id
        JOIN categories c ON sp.category_id = c.id
        WHERE (:brandIds IS NULL OR sp.brand_id IN (:brandIds))
        AND (:categoryIds IS NULL OR sp.category_id IN (:categoryIds))
        AND (:name IS NULL OR MATCH(sp.name) AGAINST (CONCAT('+', :name, '*') IN BOOLEAN MODE))
        GROUP BY sp.id, sp.name, sp.description, sp.image_url, sp.slug,
                 sp.brand_id, b.name, sp.category_id, c.name, sp.shop_id, sk.discount
        ORDER BY price DESC
    """, nativeQuery = true)
    Page<SpuDetailResponse> findByBrandAndCategorySortedDesc(
            @Param("name") String name,
            @Param("brandIds") List<String> brandIds,
            @Param("categoryIds") List<String> categoryIds,
            Pageable pageable);

}
