package com.digitalbanking.entity.auth;

import com.digitalbanking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String deviceFingerprint;

    @Column(length = 200)
    private String deviceName;

    @Column(length = 100)
    private String browser;

    @Column(length = 100)
    private String operatingSystem;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime lastActiveAt;

    @Column(nullable = false)
    private Boolean trusted = false;
}
