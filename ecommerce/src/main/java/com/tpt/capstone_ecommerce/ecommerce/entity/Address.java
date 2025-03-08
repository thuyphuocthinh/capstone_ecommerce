package com.tpt.capstone_ecommerce.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, name = "specific_address")
    @NotBlank(message = "Specific address cannot be blank")
    @Size(min = 1, max = 255, message = "Specific address length is invalid")
    private String specificAddress;

    @Column(nullable = false, name = "full_name")
    @NotBlank(message = "Fullname of address cannot be blank")
    @Size(min = 1, max = 255, message = "Fullname of address length is invalid")
    private String fullName;

    @Column(nullable = false, name = "phone", unique = true, length = 11)
    @NotBlank(message = "Address phone cannot be blank")
    private String phone;

    // @Column(nullable = false, name = "is_default")
    // private boolean isDefault = false;

    // location
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // user id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
