package com.tpt.capstone_ecommerce.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(nullable = false, name = "cart_id")
    private Cart cart;

    @Column(nullable = false, name = "quantity")
    @Min(value = 0, message = "Cart item quantity must be greater than or equal to zero")
    private int quantity = 0;

    @Column(nullable = false, name = "unit_price")
    @Min(value = 0, message = "Cart item unit price must be greater than or equal to zero")
    private double unitPrice = 0.0;

    @Column(nullable = false, name = "discount")
    @Min(value = 0, message = "Cart item discount must be greater than or equal to zero")
    private double discount = 0.0;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public double getTotalPrice() {
        return this.unitPrice * this.quantity;
    }
}
