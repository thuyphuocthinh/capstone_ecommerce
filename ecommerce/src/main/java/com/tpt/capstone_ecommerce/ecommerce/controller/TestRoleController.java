package com.tpt.capstone_ecommerce.ecommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class TestRoleController {
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/customer")
    public String roleCustomer() {
        return "Customer";
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/seller")
    public String roleSeller() {
        return "Seller";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String roleAdmin() {
        return "Admin";
    }
}
