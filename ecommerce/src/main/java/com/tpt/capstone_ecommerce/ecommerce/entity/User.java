package com.tpt.capstone_ecommerce.ecommerce.entity;

import com.tpt.capstone_ecommerce.ecommerce.enums.USER_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, name = "first_name")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 255, message = "First name length is invalid")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 255, message = "Last name length is invalid")
    private String lastName;

    @Column(nullable = false, unique = true, name = "email")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email is too long")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status", length = 10)
    @NotNull(message = "Status cannot be null")
    private USER_STATUS status = USER_STATUS.PENDING;

    @Column(nullable = false, name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false),
            name = "user_roles"
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

//    // Ensure updatedAt is updated before stored to db
//    @PreUpdate
//    public void preUpdate() {
//        updatedAt = LocalDateTime.now();
//    }

    // Ensure status of entity won't be null before stored to db
    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = USER_STATUS.PENDING;
        }
    }
}
