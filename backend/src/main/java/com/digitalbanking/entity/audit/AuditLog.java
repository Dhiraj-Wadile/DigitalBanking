package com.digitalbanking.entity.audit;

import com.digitalbanking.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_date", columnList = "actionDate"),
    @Index(name = "idx_audit_entity", columnList = "entityType")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 100)
    private String entityType;

    private Long entityId;

    @Column(length = 5000)
    private String oldValues;

    @Column(length = 5000)
    private String newValues;

    @Column(length = 200)
    private String ipAddress;

    @Column(length = 500)
    private String deviceInfo;

    @Column(length = 100)
    private String sessionId;

    @Column(nullable = false)
    private LocalDateTime actionDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuditActionStatus status = AuditActionStatus.SUCCESS;

    @Column(length = 2000)
    private String failureReason;

    public enum AuditActionStatus {
        SUCCESS, FAILED, BLOCKED
    }
}
