package com.example.reto_backend_febrero2026.audit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Async("taskExecutor")
    public void saveAuditLog(String traceId, String module, String action, String message, String detail, String level) {
        AuditLog log = new AuditLog(traceId, module, action, message, detail, level);
        auditRepository.save(log);
    }

}
