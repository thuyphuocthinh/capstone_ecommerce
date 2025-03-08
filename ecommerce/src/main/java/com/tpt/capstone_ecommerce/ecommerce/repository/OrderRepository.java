package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Order;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findAllByUser(User user, Pageable pageable);
}
