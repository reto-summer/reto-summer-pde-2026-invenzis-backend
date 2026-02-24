package com.example.reto_backend_febrero2026.audit;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@org.hibernate.annotations.Immutable
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trace_id", updatable = false)
    private String traceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Column(nullable = false, updatable = false)
    private String module;

    @Column(nullable = false, updatable = false)
    private String action;

    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT", nullable = false, updatable = false)
    private String detail;

    @Column(nullable = false, updatable = false)
    private String level;




    public AuditLog() {
    }

    public AuditLog(String traceId, String module, String action, String message, String details, String level) {
        this.traceId = traceId;
        this.module = module;
        this.action = action;
        this.message = message;
        this.detail = details;
        this.level = level;
        this.date = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getTraceId() {
        return traceId;
    }
    public String getModule() {
        return module;
    }

    public String getAction() {
        return action;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    public String getLevel() {
        return level;
    }
}
