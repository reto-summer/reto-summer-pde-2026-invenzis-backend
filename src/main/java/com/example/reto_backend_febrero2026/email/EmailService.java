package com.example.reto_backend_febrero2026.email;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
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
    private final EmailMapper emailMapper;

    @Value("${mail.to}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine,
                        IEmailRepository emailRepository, EmailMapper emailMapper) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
    }

    @Override
    public List<EmailDTO> findAllActive() {
        return emailRepository.findByActivoTrue().stream()
                .map(emailMapper::emailToEmailDTO)
                .toList();
    }

    @Override
    public Optional<EmailDTO> findById(String emailAddress) {
        Email email = emailRepository.findById(emailAddress).orElseThrow(() -> new EntityNotFoundException("Subfamilia no encontrada"));
        return Optional.ofNullable(emailMapper.emailToEmailDTO(email));
    }

    @Override
    public EmailDTO create(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        Optional<Email> existingEmail = emailRepository.findById(normalizedEmail);
        
        if (existingEmail.isPresent()) {
            Email emailEntity = existingEmail.get();

            if (emailEntity.getActivo()) {
                log.info("El email {} ya existe y está activo", normalizedEmail);
                return emailMapper.emailToEmailDTO(emailEntity);
            }

            log.info("Reactivando email {}", normalizedEmail);
            emailRepository.updateActivo(normalizedEmail, true);

            return emailRepository.findById(normalizedEmail)
                    .map(emailMapper::emailToEmailDTO)
                    .orElseThrow(() -> new IllegalArgumentException("Error al recuperar email reactivado"));
        }

        log.info("Creando nuevo email {}", normalizedEmail);
        return emailMapper.emailToEmailDTO(emailRepository.save(new Email(normalizedEmail)));
    }

    @Override
    public EmailDTO update(String emailAddress, Boolean activo) {
        if (!emailRepository.existsById(emailAddress)) {
            throw new IllegalArgumentException("Destino de email no encontrado: " + emailAddress);
        }

        if (activo != null) {
            emailRepository.updateActivo(emailAddress, activo);
        }

        return emailRepository.findById(emailAddress)
                .map(emailMapper::emailToEmailDTO)
                .orElseThrow(() -> new IllegalArgumentException("Error al recuperar email actualizado"));
    }

    @Override
    public void deactivate(String emailAddress) {
        if (!emailRepository.existsById(emailAddress)) {
            throw new IllegalArgumentException ("Destino de email no encontrado: " + emailAddress);
        }

        emailRepository.updateActivo(emailAddress, false);
    }

    @Override
    public List<String> findAllActiveEmails() {
        return emailRepository.findAllActiveEmails();
    }

    @Async
    @Auditable(module = "EMAIL_SERVICE", action = "SEND_MAIL")
    @Override
    public void sendLicitacionesEmail(List<LicitacionDTO> licitaciones) {
        List<LicitacionDTO> safeItems = licitaciones == null ? List.of() : licitaciones;
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
                .filter(dto -> dto.getIdLicitacion() != null)
                .map(dto -> String.valueOf(dto.getIdLicitacion()))
                .collect(java.util.stream.Collectors.joining(", "));

        List<Email> emailsFromDb = emailRepository.findByActivoTrue();
        String[] recipients;

        if (!emailsFromDb.isEmpty()) {
            recipients = emailsFromDb.stream().map(Email::getEmailAddress).toArray(String[]::new);
            log.info("Usando {} emails de la base de datos", emailsFromDb.size());
        } else {
            recipients = parseEmailList(mailTo);
            log.info("Usando emails de application.properties");
        }

        MDC.put("notificationContent", licitacionIds.isBlank() ? "sin IDs" : licitacionIds);

        if (recipients.length == 0) {
            MDC.put("notificationSuccess", "false");
            MDC.put("notificationDetail", "Sin destinatarios válidos en BD ni mail.to");
            log.error("No hay destinatarios válidos configurados en BD ni en mail.to");
            return;
        }

        try {
            MDC.put("notificationDetail", "Envío exitoso");
            MDC.put("notificationSuccess", "true");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de licitaciones enviado a {} destinatarios con {} licitaciones", recipients.length, safeItems.size());
        } catch (Exception e) {
            MDC.put("notificationSuccess", "false");
            MDC.put("notificationDetail", e.getMessage());
            log.error("Error al enviar email de licitaciones: {}", e.getMessage(), e);
            throw new RuntimeException("ERROR en envío de mail: " + e);
        }
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