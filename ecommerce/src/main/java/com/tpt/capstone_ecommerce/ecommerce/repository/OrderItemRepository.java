package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    @Query(value = """
                SELECT * FROM order_items oi 
                JOIN skus sk ON oi.sku_id = sk.sku_id
                JOIN spus sp ON sk.spu_id = sp.id
                JOIN shops sh ON sp.shop_id = sh.id
                WHERE shop_id = :shop_id
            """,
            nativeQuery = true)
    Page<OrderItem> findAllByShopId(@Param("shopId") String shopId, Pageable pageable);
}
