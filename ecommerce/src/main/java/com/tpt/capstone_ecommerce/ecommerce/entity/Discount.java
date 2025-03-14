package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.DISCOUNT_TYPE;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, name = "name")
    @NotBlank(message = "Discount name cannot be blank")
    @Size(min = 1, max = 255, message = "Discount name length is invalid")
    private String name;

    @Column(nullable = false, unique = true, length = 50, name = "code")
    @NotBlank(message = "Discount code cannot be blank")
    @Size(min = 1, max = 50, message = "Discount code length is invalid")
    private String code;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT", name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false, name = "creator_id")
    private User creator;

    @Column(nullable = false, name = "value")
    @Min(value = 0, message = "Discount value must be greater than or equal to zero")
    private double value = 0.0;

    @Column(nullable = false, name = "min_order_value")
    @Min(value = 0, message = "Discount min order value must be greater than or equal to zero")
    private double minOrderValue = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15, name = "status")
    @NotNull(message = "Discount status cannot be null")
    private DISCOUNT_STATUS status = DISCOUNT_STATUS.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15, name = "type")
    @NotNull(message = "Discount type cannot be null")
    private DISCOUNT_TYPE type;

    @Column(nullable = false, name = "start_date", updatable = false)
    private LocalDateTime startDate;

    @Column(nullable = false, name = "end_date", updatable = false)
    private LocalDateTime endDate;

    @Column(nullable = false, name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(30);
        }
        if(status == null) {
            status = DISCOUNT_STATUS.ACTIVE;
        }
    }
}

