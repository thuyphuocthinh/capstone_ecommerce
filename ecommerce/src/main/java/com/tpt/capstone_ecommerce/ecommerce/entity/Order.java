package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.ORDER_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.enums.USER_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, name = "total_price")
    @Min(value = 0, message = "Order total price must be greater than or equal to zero")
    private double totalPrice = 0.0;

    @Column(nullable = false, name = "final_total_price")
    @Min(value = 0, message = "Order final total price must be greater than or equal to zero")
    private double finalTotalPrice = 0.0;

    @Column(nullable = false, name = "total_quantity")
    @Min(value = 0, message = "Order total quantity must be greater than or equal to zero")
    private int totalQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15, name = "status")
    @NotBlank(message = "Order status cannot be blank")
    private ORDER_STATUS status = ORDER_STATUS.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15, name = "status")
    @NotBlank(message = "Payment method cannot be blank")
    private PAYMENT_METHOD paymentMethod;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(nullable = false, name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ORDER_STATUS.PENDING;
        }
    }
}
