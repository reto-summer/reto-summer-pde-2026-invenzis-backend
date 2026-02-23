package com.example.reto_backend_febrero2026.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditLog, Integer> {
}
