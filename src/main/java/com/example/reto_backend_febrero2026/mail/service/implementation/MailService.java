package com.example.reto_backend_febrero2026.mail.service.implementation;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.mail.MailModel;
import com.example.reto_backend_febrero2026.mail.repository.interfaces.IMailRepository;
import com.example.reto_backend_febrero2026.mail.service.interfaces.IMailService;
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
public class MailService implements IMailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final IMailRepository mailRepository;

    @Value("${mail.to}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine, IMailRepository mailRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailRepository = mailRepository;
    }

    @Override
    public List<MailModel> findAllActive() {
        return mailRepository.findByActivoTrue();
    }

    @Override
    public Optional<MailModel> findById(Long id) {
        return mailRepository.findById(id);
    }

    @Override
    public MailModel create(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (mailRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("El email ya existe en la base de datos");
        }
        return mailRepository.save(new MailModel(normalizedEmail));
    }

    @Override
    public MailModel update(Long id, String email, Boolean activo) {
        MailModel destination = mailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino de email no encontrado"));

        if (email != null && !email.trim().isEmpty()) {
            String normalizedEmail = email.trim().toLowerCase();
            if (!normalizedEmail.matches(EMAIL_REGEX)) {
                throw new IllegalArgumentException("El formato del email no es válido");
            }
            if (!destination.getEmail().equals(normalizedEmail) && mailRepository.existsByEmail(normalizedEmail)) {
                throw new IllegalStateException("El email ya existe en la base de datos");
            }
            destination.setEmail(normalizedEmail);
        }

        if (activo != null) {
            destination.setActivo(activo);
        }

        return mailRepository.save(destination);
    }

    @Override
    public void deactivate(Long id) {
        MailModel destination = mailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino de email no encontrado"));
        destination.setActivo(false);
        mailRepository.save(destination);
    }

    @Override
    public List<String> findAllActiveEmails() {
        return mailRepository.findAllActiveEmails();
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
        MDC.put("notificationContent", htmlContent);

        String[] recipients = getEmailRecipients();

        if (recipients.length == 0) {
            MDC.put("notificationSuccess", "false");
            MDC.put("notificationDetail", "Sin destinatarios válidos en BD ni mail.to");
            log.error("No hay destinatarios válidos configurados en BD ni en mail.to");
            return;
        }

        try {
            String recipientsList = String.join(", ", recipients);
            MDC.put("notificationDetail", "Enviado a: " + recipientsList);
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
        List<String> emailsFromDb = mailRepository.findAllActiveEmails();
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