package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.SKU_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.SPU_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "spus")
public class Spu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, name = "name")
    @NotBlank(message = "SPU name cannot be blank")
    @Size(min = 1, max = 255, message = "SPU name length is invalid")
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(nullable = false, name = "slug", unique = true)
    @NotBlank(message = "SPU slug cannot be null")
    @Size(min = 1, max = 255, message = "SPU slug length is invalid")
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, name = "status")
    @NotNull(message = "Status cannot be null")
    private SPU_STATUS status = SPU_STATUS.ACTIVE;

    @Column(nullable = false, name = "image_url", length = 500)
    @NotBlank(message = "SPU image cannot be blank")
    @Size(max = 500, message = "SPU image URL is too long")
    @Pattern(regexp = "^(https?:\\/\\/)?([\\w-]+\\.)+[\\w-]{2,}(\\/.*)?$",
            message = "Invalid URL format")
    private String imageUrl;


    // shop
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // brand
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "spu", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sku> skus;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = SPU_STATUS.ACTIVE;
        }
    }
}
