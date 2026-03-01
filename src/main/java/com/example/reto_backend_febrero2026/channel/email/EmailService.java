package com.example.reto_backend_febrero2026.channel.email;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.channel.IChannel;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionService;
import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.MDC;

@Service
public class EmailService implements IEmailService, IChannel {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);


    private final JavaMailSender mailSender;
    private final EmailValidator emailValidator;
    private final EmailTemplateService emailTemplateService;
    private final IEmailRepository emailRepository;
    private final EmailMapper emailMapper;
    private final LicitacionService

    @Value("${mail.to}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender, EmailValidator emailValidator,
                        EmailTemplateService emailTemplateService, IEmailRepository emailRepository,
                        EmailMapper emailMapper) {
        this.mailSender = mailSender;
        this.emailValidator = emailValidator;
        this.emailTemplateService = emailTemplateService;
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
    }

    @Override
    public List<EmailDTO> findAllActive() {
        return emailRepository.findByActivoTrue().stream()
                .map(emailMapper::emailToDTO)
                .toList();
    }

    @Override
    public EmailDTO findById(String emailAddress) {
        Email email = emailRepository.findById(emailAddress)
                .orElseThrow(() -> new EntityNotFoundException("Email no encontrado: " + emailAddress));
        return emailMapper.emailToDTO(email);
    }

    @Override
    @Transactional
    public EmailDTO create(String emailAdress) {
        String normalizedEmail = emailValidator.normalize(emailAdress);
        emailValidator.validateOrThrow(normalizedEmail);

        return emailRepository.findById(normalizedEmail)
                .map(this::handleExistingEmail)
                .orElseGet(()-> createNewMail(normalizedEmail));
    }

    private EmailDTO handleExistingEmail(Email emailEntity){
        if (emailEntity.isActivo()) {
            log.info("El email {} ya existe y esta activo", emailEntity.getEmailAddress());
            return emailMapper.emailToDTO(emailEntity);
        }

        log.info("Reactivando email {}", emailEntity.getEmailAddress());
        emailRepository.updateActivo(emailEntity.getEmailAddress(), true);

        emailEntity.setActivo(true);
        return emailMapper.emailToDTO(emailEntity);
    }

    private EmailDTO createNewMail(String emailAddress) {
        log.info("Creando mail {}", emailAddress);
        Email emailEntity = new Email(emailAddress);
        return emailMapper.emailToDTO(emailRepository.save(emailEntity));
    }

    @Override
    @Transactional
    public EmailDTO update(String emailAddress, Boolean activo) {
        EmailDTO existingEmail = findById(emailAddress);

        if(activo != null && !activo.equals(existingEmail.getActivo())) {
            log.info("Cambiando estado de email {} a activo ={}", emailAddress, activo);
            emailRepository.updateActivo(emailAddress, activo);
            existingEmail.setActivo(activo);
        }
        return existingEmail;
    }

    @Override
    public void deactivate(String emailAddress) {
        update(emailAddress, false);
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

    @Override
    public void sendNotification(){
        List<LicitacionDTO> licitacionesParaEsteEMail =
    }
}