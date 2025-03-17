package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.SHOP_STATUS;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "shops")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, name = "name")
    @NotBlank(message = "Shop name cannot be blank")
    @Size(min = 1, max = 255, message = "Shop name length is invalid")
    private String name;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(nullable = false, name = "image_url", length = 500)
    @NotBlank(message = "Shop image cannot be blank")
    @Size(max = 500, message = "Shop image URL is too long")
    @Pattern(regexp = "^(https?:\\/\\/)?([\\w-]+\\.)+[\\w-]{2,}(\\/.*)?$",
            message = "Invalid URL format")
    private String imageUrl;


    @Column(nullable = false, name = "phone", unique = true, length = 11)
    @NotBlank(message = "Shop phone cannot be blank")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status", length = 10)
    @NotNull(message = "Status cannot be null")
    private SHOP_STATUS status = SHOP_STATUS.PENDING;

    @Column(nullable = false, name = "specific_address")
    @NotBlank(message = "Specific cannot be blank")
    @Size(min = 5, max = 255, message = "Shop address's length is invalid")
    private String specificAddress;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Spu> spus = new ArrayList<>();

    // location_id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = SHOP_STATUS.PENDING;
        }
    }
}
