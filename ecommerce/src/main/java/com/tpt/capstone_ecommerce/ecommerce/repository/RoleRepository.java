package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Role;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_ROLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByRole(USER_ROLE role);
}
