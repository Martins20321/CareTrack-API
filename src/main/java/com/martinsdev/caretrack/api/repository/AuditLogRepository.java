package com.martinsdev.caretrack.api.repository;

import com.martinsdev.caretrack.api.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
