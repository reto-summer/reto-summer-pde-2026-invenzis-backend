package com.example.reto_backend_febrero2026.audit;

public interface IAuditService {

    void saveAuditLog(String traceId, String module, String action, String message, String detail, String level);
}
