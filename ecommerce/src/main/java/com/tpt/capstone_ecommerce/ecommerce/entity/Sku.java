package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.SKU_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "skus")
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, name = "name")
    @NotBlank(message = "SPU name cannot be blank")
    @Size(min = 1, max = 255, message = "SPU name length is invalid")
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(nullable = false, name = "quantity")
    @Min(value = 0, message = "SKU quantity must be greater than or equal to zero")
    private int quantity = 0;

    @Column(nullable = false, name = "price")
    @Min(value = 0, message = "SKU price must be greater than or equal to zero")
    private double price = 0.0;

    @Column(nullable = false, name = "discount")
    @Min(value = 0, message = "SKU discount must be greater than or equal to zero")
    private double discount = 0.0;

    // shop id
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // spu id
    @ManyToOne
    @JoinColumn(name = "spu_id")
    private Spu spu;

    @Column(length = 50, name = "color")
    private String color;

    @Column(length = 50, name = "size")
    private String size;

    @Column(nullable = false, name = "image_url", length = 500)
    @NotBlank(message = "SKU image cannot be blank")
    @Size(max = 500, message = "SKU image URL is too long")
    @Pattern(regexp = "^(https?:\\/\\/)?([\\w-]+\\.)+[\\w-]{2,}(\\/.*)?$",
            message = "Invalid URL format")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, name = "status")
    @NotNull(message = "Status cannot be null")
    private SKU_STATUS status = SKU_STATUS.ACTIVE;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = SKU_STATUS.ACTIVE;
        }
    }
}
