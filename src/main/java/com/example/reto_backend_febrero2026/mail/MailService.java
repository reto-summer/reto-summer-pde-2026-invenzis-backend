package com.example.reto_backend_febrero2026.mail;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired(required = false)
    private MailRepository mailRepository;

    @Value("${mail.to}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    private String[] parseEmailList(String emailString) {
        if (emailString == null || emailString.trim().isEmpty()) {
            log.warn("Lista de emails vacía");
            return new String[0];
        }

        return Arrays.stream(emailString.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .filter(email -> email.matches(EMAIL_REGEX))
                .toArray(String[]::new);
    }
    
    private String[] getEmailRecipients() {
        if (mailRepository != null) {
            try {
                List<String> emailsFromDb = mailRepository.findAllActiveEmails();
                if (!emailsFromDb.isEmpty()) {
                    log.info("Usando {} emails de la base de datos", emailsFromDb.size());
                    return emailsFromDb.toArray(new String[0]);
                }
            } catch (Exception e) {
                log.warn("Error al obtener emails de la base de datos, usando fallback de properties", e);
            }
        }
        
        log.info("Usando emails de application.properties");
        return parseEmailList(mailTo);
    }

    @Async
    @Auditable(module = "EMAIL_SERVICE", action = "SEND_MAIL")
    public void sendLicitacionesEmail(List<LicitacionItemRecord> items) {
        List<LicitacionItemRecord> safeItems = items == null ? List.of() : items;
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "UY")));

        Context ctx = new Context(new Locale("es", "UY"));
        ctx.setVariable("items", safeItems);
        ctx.setVariable("fecha", fecha);

        String htmlContent = templateEngine.process("email/licitaciones", ctx);

        String[] recipients = getEmailRecipients();

        if (recipients.length == 0) {
            log.error("No hay destinatarios válidos configurados en BD ni en mail.to");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(recipients);
            String subject = safeItems.isEmpty()
                    ? "Sin nuevas licitaciones ARCE - " + fecha
                    : safeItems.size() + " Licitaciones ARCE - " + fecha;
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de licitaciones enviado a {} destinatarios con {} ítems", recipients.length, safeItems.size());
        } catch (Exception e) {
            log.error("Error al enviar email de licitaciones: {}", e.getMessage(), e);
            throw new RuntimeException("ERROR en envío de mail: " + e);
        }
    }
}
