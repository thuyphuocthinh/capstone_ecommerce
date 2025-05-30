package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_METHOD;
import com.tpt.capstone_ecommerce.ecommerce.enums.PAYMENT_STATUS;
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
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_method", length = 15)
    @NotNull(message = "Payment method cannot be blank")
    private PAYMENT_METHOD paymentMethod = PAYMENT_METHOD.CASH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_status", length = 15)
    @NotNull(message = "Payment method cannot be blank")
    private PAYMENT_STATUS paymentStatus = PAYMENT_STATUS.PENDING;

    @Column(nullable = false, name = "total_price")
    @Min(value = 0, message = "Min total price is 0")
    private double totalPrice;

    @Column(nullable = true, unique = true, name = "transaction_id")
    private String transactionId;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (paymentMethod == null) {
            paymentMethod = PAYMENT_METHOD.CASH;
        }
        if (paymentStatus == null) {
            paymentStatus = PAYMENT_STATUS.PENDING;
        }
    }
}
