package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_ITEM_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(nullable = false, name = "order_id")
    private Order order;

    @Column(nullable = false, name = "quantity")
    @Min(value = 0, message = "Order item quantity must be greater than or equal to zero")
    private int quantity = 0;

    @Column(nullable = false, name = "price")
    @Min(value = 0, message = "Order item price must be greater than or equal to zero")
    private double price = 0.0;

    @Column(nullable = false, name = "discount")
    @Min(value = 0, message = "Order item discount must be greater than or equal to zero")
    private double discount = 0.0;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15, name = "status")
    @NotNull(message = "Order item status cannot be blank")
    private ORDER_ITEM_STATUS status = ORDER_ITEM_STATUS.PENDING;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ORDER_ITEM_STATUS.PENDING;
        }
    }
}
