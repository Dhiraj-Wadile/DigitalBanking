package com.digitalbanking.repository;

import com.digitalbanking.entity.audit.AuditLog;
import com.digitalbanking.entity.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUserOrderByActionDateDesc(User user, Pageable pageable);

    Page<AuditLog> findByActionOrderByActionDateDesc(String action, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.actionDate BETWEEN :startDate AND :endDate ORDER BY a.actionDate DESC")
    Page<AuditLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
