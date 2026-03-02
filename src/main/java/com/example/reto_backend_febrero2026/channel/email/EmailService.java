package com.example.reto_backend_febrero2026.channel.email;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.channel.IChannel;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionMapper;
import com.example.reto_backend_febrero2026.licitacion_email.ILicitacionEmailService;
import com.example.reto_backend_febrero2026.licitacion_email.LicitacionEmail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class EmailService implements IEmailService, IChannel {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailTemplateService templateService;
    private final IEmailRepository emailRepository;
    private final EmailMapper emailMapper;
    private final ILicitacionEmailService licitacionEmailService;
    private final EmailTransportService emailTransportService;
    private final EmailValidator emailValidator;
    private final LicitacionMapper  licitacionMapper;

    public EmailService(EmailTemplateService templateService,
                        IEmailRepository emailRepository,
                        EmailMapper emailMapper,
                        ILicitacionEmailService licitacionEmailService,
                        EmailRecipientResolver recipientResolver,
                        EmailTransportService emailTransportService,
                        EmailValidator emailValidator,
                        LicitacionMapper licitacionMapper) {
        this.templateService = templateService;
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
        this.licitacionEmailService = licitacionEmailService;
        this.emailTransportService = emailTransportService;
        this.emailValidator = emailValidator;
        this.licitacionMapper = licitacionMapper;
    }

    @Override
    public List<EmailDTO> findAllActive() {
        return emailRepository.findByActivoTrue().stream()
                .map(emailMapper::emailToDTO)
                .toList();
    }

    @Override
    public EmailDTO findById(String DireccionEmail) {
        Email email = emailRepository.findById(DireccionEmail)
                .orElseThrow(() -> new EntityNotFoundException("Email no encontrado: " + DireccionEmail));
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
            log.info("El email {} ya existe y esta activo", emailEntity.getDireccionEmail());
            return emailMapper.emailToDTO(emailEntity);
        }

        log.info("Reactivando email {}", emailEntity.getDireccionEmail());
        emailRepository.updateActivo(emailEntity.getDireccionEmail(), true);

        emailEntity.setActivo(true);
        return emailMapper.emailToDTO(emailEntity);
    }

    private EmailDTO createNewMail(String DireccionEmail) {
        log.info("Creando mail {}", DireccionEmail);
        Email emailEntity = new Email(DireccionEmail);
        return emailMapper.emailToDTO(emailRepository.save(emailEntity));
    }

    @Override
    @Transactional
    public EmailDTO update(String DireccionEmail, Boolean activo) {
        EmailDTO existingEmail = findById(DireccionEmail);

        if(activo != null && !activo.equals(existingEmail.getActivo())) {
            log.info("Cambiando estado de email {} a activo ={}", DireccionEmail, activo);
            emailRepository.updateActivo(DireccionEmail, activo);
            existingEmail.setActivo(activo);
        }
        return existingEmail;
    }

    @Override
    public void deactivate(String DireccionEmail) {
        update(DireccionEmail, false);
    }

    @Override
    public List<String> findAllActiveEmails() {
        return emailRepository.findAllActiveEmails();
    }

    @Override
    @Async
    @Auditable(module = "EMAIL_SERVICE", action = "SEND_MAIL")
    public void sendNotification() {

        List<LicitacionEmail> pendientes =
                licitacionEmailService.getPendientes();

        if (pendientes.isEmpty()) {
            log.info("No hay notificaciones pendientes para enviar.");
            return;
        }

        Map<String, List<LicitacionEmail>> pendientesPorEmail =
                pendientes.stream()
                        .collect(Collectors.groupingBy(
                                le -> le.getEmail().getDireccionEmail()));

        for (Map.Entry<String, List<LicitacionEmail>> entry : pendientesPorEmail.entrySet()) {

            String email = entry.getKey();
            List<LicitacionEmail> registros = entry.getValue();

            List<LicitacionDTO> licitaciones =
                    registros.stream()
                            .map(LicitacionEmail::getLicitacion)
                            .map(licitacionMapper::licitacionToLicitacionDTO)
                            .toList();

            if (licitaciones.isEmpty()) {
                continue;
            }

            try {
                String html = templateService.generarLicitacionesHtml(
                        licitaciones,
                        LocalDateTime.now()
                );

                String subject =  licitaciones.size() + " Licitaciones ARCE - " + LocalDate.now();

                emailTransportService.sendHtmlEmail(List.of(email), subject, html);

                registros.forEach(LicitacionEmail::marcarComoEnviado);

                log.info("Email enviado a {} con {} licitaciones", email, licitaciones.size());

            } catch (Exception e) {
                log.error("Error enviando mail a {}: {}", email, e.getMessage(), e);
            }
        }
    }
}