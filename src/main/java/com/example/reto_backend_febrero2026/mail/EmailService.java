package com.example.reto_backend_febrero2026.mail;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;
import org.slf4j.MDC;

@Service
public class EmailService implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final IEmailRepository emailRepository;

    @Value("${mail.to}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, IEmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailRepository = emailRepository;
    }

    @Override
    public List<Email> findAllActive() {
        return emailRepository.findByActivoTrue();
    }

    @Override
    public Optional<Email> findById(String emailAddress) {
        return emailRepository.findById(emailAddress);
    }

    @Override
    public Email create(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (emailRepository.existsById(normalizedEmail)) {
            throw new IllegalStateException("El email ya existe en la base de datos");
        }
        return emailRepository.save(new Email(normalizedEmail));
    }

    @Override
    public Email update(String emailAddress, Boolean activo) {
        Email destination = emailRepository.findById(emailAddress)
                .orElseThrow(() -> new RuntimeException("Destino de email no encontrado"));

        if (activo != null) {
            destination.setActivo(activo);
        }

        return emailRepository.save(destination);
    }

    @Override
    public void deactivate(String emailAddress) {
        Email destination = emailRepository.findById(emailAddress)
                .orElseThrow(() -> new RuntimeException("Destino de email no encontrado"));
        destination.setActivo(false);
        emailRepository.save(destination);
    }

    @Override
    public List<String> findAllActiveEmails() {
        return emailRepository.findAllActiveEmails();
    }

    @Async
    @Auditable(module = "EMAIL_SERVICE", action = "SEND_MAIL")
    @Override
    public void sendLicitacionesEmail(List<LicitacionItemRecord> items) {
        List<LicitacionItemRecord> safeItems = items == null ? List.of() : items;
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "UY")));
        String subject = safeItems.isEmpty()
                ? "Sin nuevas licitaciones ARCE - " + fecha
                : safeItems.size() + " Licitaciones ARCE - " + fecha;

        Context ctx = new Context(new Locale("es", "UY"));
        ctx.setVariable("items", safeItems);
        ctx.setVariable("fecha", fecha);

        String htmlContent = templateEngine.process("email/licitaciones", ctx);

        // Set notification context for AuditAspect
        MDC.put("notificationTitle", subject);

        String licitacionIds = safeItems.stream()
                .filter(item -> item.idLicitacion() != null)
                .map(item -> String.valueOf(item.idLicitacion()))
                .collect(java.util.stream.Collectors.joining(", "));

        List<Email> emailsFromDb = emailRepository.findByActivoTrue();
        String[] recipients;
        String emailIds;

        if (!emailsFromDb.isEmpty()) {
            recipients = emailsFromDb.stream().map(Email::getEmailAddress).toArray(String[]::new);
            emailIds = emailsFromDb.stream()
                    .map(Email::getEmailAddress)
                    .collect(java.util.stream.Collectors.joining(", "));
            log.info("Usando {} emails de la base de datos", emailsFromDb.size());
        } else {
            recipients = parseEmailList(mailTo);
            emailIds = "mailTo";
            log.info("Usando emails de application.properties");
        }

        MDC.put("notificationContent", emailIds);

        if (recipients.length == 0) {
            MDC.put("notificationSuccess", "false");
            MDC.put("notificationDetail", "Sin destinatarios válidos en BD ni mail.to");
            log.error("No hay destinatarios válidos configurados en BD ni en mail.to");
            return;
        }

        try {
            MDC.put("notificationDetail", licitacionIds.isBlank() ? "sin IDs" : licitacionIds);
            MDC.put("notificationSuccess", "true");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de licitaciones enviado a {} destinatarios con {} ítems", recipients.length, safeItems.size());
        } catch (Exception e) {
            MDC.put("notificationSuccess", "false");
            MDC.put("notificationDetail", e.getMessage());
            log.error("Error al enviar email de licitaciones: {}", e.getMessage(), e);
            throw new RuntimeException("ERROR en envío de mail: " + e);
        }
    }

    private String[] getEmailRecipients() {
        List<String> emailsFromDb = emailRepository.findAllActiveEmails();
        if (!emailsFromDb.isEmpty()) {
            log.info("Usando {} emails de la base de datos", emailsFromDb.size());
            return emailsFromDb.toArray(new String[0]);
        }
        log.info("Usando emails de application.properties");
        return parseEmailList(mailTo);
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
}