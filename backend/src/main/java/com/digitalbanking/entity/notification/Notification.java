package com.digitalbanking.entity.notification;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notif_user", columnList = "user_id"),
    @Index(name = "idx_notif_read", columnList = "isRead")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @Column(length = 500)
    private String actionUrl;

    @Column(length = 500)
    private String metadata;

    @Column(nullable = false)
    private Boolean sent = false;

    private LocalDateTime sentAt;

    public enum NotificationType {
        TRANSACTION, PAYMENT, SECURITY, PROMOTIONAL,
        SYSTEM, KYC, LOAN, CARD, ACCOUNT
    }

    public enum NotificationChannel {
        IN_APP, EMAIL, SMS, PUSH, WHATSAPP
    }
}
