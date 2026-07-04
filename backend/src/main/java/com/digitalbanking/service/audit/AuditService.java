package com.digitalbanking.service.audit;

import com.digitalbanking.entity.audit.AuditLog;
import com.digitalbanking.entity.auth.User;
import com.digitalbanking.repository.AuditLogRepository;
import com.digitalbanking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    @Async
    public void logAction(String action, String entityType, Long entityId, String details) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            User user = userId != null ? new User() {{ setId(userId); }} : null;

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .newValues(details)
                    .actionDate(LocalDateTime.now())
                    .status(AuditLog.AuditActionStatus.SUCCESS)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage());
        }
    }
}
