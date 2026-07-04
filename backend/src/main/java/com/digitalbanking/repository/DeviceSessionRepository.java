package com.digitalbanking.repository;

import com.digitalbanking.entity.auth.DeviceSession;
import com.digitalbanking.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {

    List<DeviceSession> findByUserAndActiveTrue(User user);

    Optional<DeviceSession> findByUserAndDeviceFingerprint(User user, String fingerprint);
}
