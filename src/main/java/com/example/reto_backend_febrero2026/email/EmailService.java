package com.example.reto_backend_febrero2026.email;

import com.example.reto_backend_febrero2026.channel.licitacion_email.EmailTemplateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class EmailService implements IEmailService{
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailTemplateService templateService;
    private final IEmailRepository emailRepository;
    private final EmailMapper emailMapper;
    private final EmailValidator emailValidator;

    public EmailService(EmailTemplateService templateService, IEmailRepository emailRepository,
                        EmailMapper emailMapper, EmailValidator emailValidator) {
        this.templateService = templateService;
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
        this.emailValidator = emailValidator;
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
}