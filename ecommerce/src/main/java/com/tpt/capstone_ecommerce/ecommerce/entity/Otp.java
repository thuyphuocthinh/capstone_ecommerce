package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.OTP_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.enums.SHOP_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "otps")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, name = "user_email")
    @Email(message = "OTP user email is invalid")
    private String userEmail;

    @Column(nullable = false, updatable = false, unique = true, length = 50, name = "otp")
    @NotBlank(message = "OTP cannot be blank")
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, name = "status")
    private OTP_STATUS status = OTP_STATUS.PENDING;

    @Column(nullable = false, name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(nullable = false, updatable = false, name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = OTP_STATUS.PENDING;
        }
    }
}
