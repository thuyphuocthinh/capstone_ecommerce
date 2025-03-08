package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Address;
import com.tpt.capstone_ecommerce.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Page<Address> findAllByUser(User user, Pageable pageable);
}
