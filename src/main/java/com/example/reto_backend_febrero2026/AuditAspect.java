package com.example.reto_backend_febrero2026;

import com.example.reto_backend_febrero2026.audit.AuditService;
import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.notificacion.INotificacionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private static final String TRACE_KEY = "traceId";
    private static final String NOTIF_TITLE_KEY = "notificationTitle";
    private static final String NOTIF_CONTENT_KEY = "notificationContent";
    private static final String NOTIF_DETAIL_KEY = "notificationDetail";
    private static final String NOTIF_SUCCESS_KEY = "notificationSuccess";

    @Autowired
    private AuditService auditService;

    @Autowired
    private INotificacionService notificacionService;

    @Around("@annotation(auditable)")
    public Object around(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        if (MDC.get(TRACE_KEY)==null) {
            MDC.put(TRACE_KEY, UUID.randomUUID().toString().substring(0, 8));
        }
        try{
            return joinPoint.proceed();
        } finally {
        }
    }

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logAfter(JoinPoint joinPoint, Auditable auditable, Object result) {
        String traceId = MDC.get(TRACE_KEY);
        String message = "Ejecución exitosa: " + joinPoint.getSignature().getName();
        String detail = Arrays.toString(joinPoint.getArgs());
        log.info("[{}] Módulo: {} - Mensaje: {} - Detalle: {}", traceId, auditable.module(), message, detail);
        auditService.saveAuditLog(traceId, auditable.module(), auditable.action(), message, detail,"INFO");

        // Create notification for email service
        if (isMailNotification(auditable)) {
            boolean success = !"false".equalsIgnoreCase(MDC.get(NOTIF_SUCCESS_KEY));
            String title = resolveTitle(auditable);
            String notifDetail = MDC.get(NOTIF_DETAIL_KEY);
            String content = resolveContent(joinPoint);
            
            Integer nextId = notificacionService.getNextId();
            notificacionService.create(nextId, title, success, notifDetail, content, LocalDateTime.now());
            clearNotificationContext();
        }
    }

    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Auditable auditable, Exception ex) {
        String traceId = MDC.get(TRACE_KEY);
        String message = "ERROR en " + joinPoint.getSignature().getName() + ": " + ex.getMessage();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();
        String detail = stackTrace.length() > 2000 ? stackTrace.substring(0, 2000) : stackTrace;

        log.error("[{}] Módulo: {} - Mensaje: {} - Detalle: {}", traceId, auditable.module(), message, detail);
        auditService.saveAuditLog(traceId, auditable.module(), auditable.action(), message, detail, "ERROR");

        // Create notification for email service failure
        if (isMailNotification(auditable)) {
            String title = resolveTitle(auditable);
            String notifDetail = MDC.get(NOTIF_DETAIL_KEY);
            String content = resolveContent(joinPoint);
            String resolvedDetail = (notifDetail == null || notifDetail.isBlank()) ? ex.getMessage() : notifDetail;
            
            Integer nextId = notificacionService.getNextId();
            notificacionService.create(nextId, title, false, resolvedDetail, content, LocalDateTime.now());
            clearNotificationContext();
        }
    }

    private boolean isMailNotification(Auditable auditable) {
        return "EMAIL_SERVICE".equals(auditable.module()) && "SEND_MAIL".equals(auditable.action());
    }

    private String resolveTitle(Auditable auditable) {
        String title = MDC.get(NOTIF_TITLE_KEY);
        return (title == null || title.isBlank()) ? auditable.action() : title;
    }

    private String resolveContent(JoinPoint joinPoint) {
        String content = MDC.get(NOTIF_CONTENT_KEY);
        return (content == null || content.isBlank()) ? Arrays.toString(joinPoint.getArgs()) : content;
    }

    private void clearNotificationContext() {
        MDC.remove(NOTIF_TITLE_KEY);
        MDC.remove(NOTIF_CONTENT_KEY);
        MDC.remove(NOTIF_DETAIL_KEY);
        MDC.remove(NOTIF_SUCCESS_KEY);
        MDC.remove("notificationRecipients");
    }

}