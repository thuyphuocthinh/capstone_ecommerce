package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.AUTH_PROVIDER;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auth_providers")
public class AuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(nullable = false, name = "provider_id", unique = true, length = 500)
    @NotBlank(message = "Provider id cannot be blank")
    private String providerId;

    @Column(nullable = false, unique = true, name = "email")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email is too long")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "provider", length = 10)
    @NotNull(message = "Auth provider cannot be null")
    private AUTH_PROVIDER authProvider = AUTH_PROVIDER.GOOGLE;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (authProvider == null) {
            authProvider = AUTH_PROVIDER.GOOGLE;
        }
    }
}
