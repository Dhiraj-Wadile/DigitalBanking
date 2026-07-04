package com.digitalbanking.entity.auth;

import com.digitalbanking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private Boolean enabled = false;

    @Column(nullable = false)
    private Boolean accountLocked = false;

    private Integer failedLoginAttempts = 0;

    private LocalDateTime lockedUntil;

    private LocalDateTime lastLoginAt;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(nullable = false)
    private Boolean mfaEnabled = false;

    @Column(length = 100)
    private String mfaSecret;

    @Column(length = 500)
    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    @Column(length = 500)
    private String emailVerificationToken;

    private Boolean emailVerified = false;

    @Column(length = 10)
    private String otpCode;

    private LocalDateTime otpExpiry;

    public enum UserRole {
        ROLE_CUSTOMER, ROLE_EMPLOYEE, ROLE_ADMIN, ROLE_SUPER_ADMIN
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
