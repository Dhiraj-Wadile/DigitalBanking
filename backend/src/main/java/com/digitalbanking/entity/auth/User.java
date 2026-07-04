package com.digitalbanking.entity.auth;

import com.digitalbanking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_username", columnList = "username", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountLocked = false;

    @Builder.Default
    private Integer failedLoginAttempts = 0;

    private java.time.LocalDateTime lockedUntil;

    private java.time.LocalDateTime lastLoginAt;

    @Column(length = 500)
    private String resetToken;

    private java.time.LocalDateTime resetTokenExpiry;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    public enum UserRole {
        ROLE_CUSTOMER, ROLE_EMPLOYEE, ROLE_ADMIN, ROLE_SUPER_ADMIN
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
