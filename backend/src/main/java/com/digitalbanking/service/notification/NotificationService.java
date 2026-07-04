package com.digitalbanking.service.notification;

import com.digitalbanking.entity.notification.Notification;
import com.digitalbanking.entity.auth.User;
import com.digitalbanking.repository.NotificationRepository;
import com.digitalbanking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Async
    public void sendNotification(Long userId, String title, String message,
                                 Notification.NotificationType type,
                                 Notification.NotificationChannel channel) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .channel(channel)
                .isRead(false)
                .sent(true)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Notification sent to user {}: {}", userId, title);
    }

    @Async
    public void sendTransactionNotification(Long userId, String transactionType, String amount, String accountNumber) {
        String title = transactionType + " Notification";
        String message = String.format("Your %s of %s on account %s was successful.",
                transactionType, amount, accountNumber);
        sendNotification(userId, title, message, Notification.NotificationType.TRANSACTION,
                Notification.NotificationChannel.IN_APP);
    }

    @Async
    public void sendSecurityAlert(Long userId, String message) {
        sendNotification(userId, "Security Alert", message,
                Notification.NotificationType.SECURITY, Notification.NotificationChannel.EMAIL);
    }
}
