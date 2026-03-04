package com.example.reto_backend_febrero2026.channel.licitacion_email;

import com.example.reto_backend_febrero2026.email.Email;
import com.example.reto_backend_febrero2026.email.EmailDTO;
import com.example.reto_backend_febrero2026.email.EmailMapper;
import com.example.reto_backend_febrero2026.email.IEmailService;
import com.example.reto_backend_febrero2026.licitacion.*;
import com.example.reto_backend_febrero2026.notificacion.INotificacionService;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LicitacionEmailService implements ILicitacionEmailService {

    private final ILicitacionEmailRepository licitacionEmailRepository;
    private final ILicitacionService licitacionService;
    private final IEmailService emailService;
    private final EmailMapper emailMapper;
    private final LicitacionMapper licitacionMapper;
    private final IEmailTemplateService emailTemplateService;
    private final IEmailTransportService emailTransportService;
    private final INotificacionService notificacionService;

    public LicitacionEmailService(
            ILicitacionEmailRepository licitacionEmailRepository,
            ILicitacionService licitacionService,
            IEmailService emailService,
            EmailMapper emailMapper,
            LicitacionMapper licitacionMapper,
            IEmailTransportService emailTransportService,
            IEmailTemplateService emailTemplateService,
            INotificacionService notificacionService) {

        this.licitacionEmailRepository = licitacionEmailRepository;
        this.licitacionService = licitacionService;
        this.emailService = emailService;
        this.emailMapper = emailMapper;
        this.licitacionMapper = licitacionMapper;
        this.emailTransportService = emailTransportService;
        this.emailTemplateService = emailTemplateService;
        this.notificacionService = notificacionService;
    }

    @Transactional
    @Override
    public void savePendingEmails() {
        List<LicitacionDTO> licitacionesDTO = licitacionService.findByFilters(null, null, LocalDate.now(), null, null, null);
        List<EmailDTO> emailsDTO = emailService.findAllActive();

        if (licitacionesDTO.isEmpty() || emailsDTO.isEmpty()) return;

        Set<String> existingKeys = licitacionEmailRepository.findByLicitacionesAndEmails(
                licitacionesDTO.stream().map(LicitacionDTO::getIdLicitacion).toList(),
                emailsDTO.stream().map(EmailDTO::getDireccionEmail).collect(Collectors.toSet())
        );

        List<LicitacionEmail> aCrear = new ArrayList<>();
        for (LicitacionDTO l : licitacionesDTO) {
            for (EmailDTO e : emailsDTO) {
                String key = l.getIdLicitacion() + "_" + e.getDireccionEmail();
                if (!existingKeys.contains(key)) {
                    aCrear.add(new LicitacionEmail(
                            licitacionMapper.licitacionDTOtoLicitacion(l),
                            emailMapper.emailDTOtoEmail(e)
                    ));
                }
            }
        }
        licitacionEmailRepository.saveAll(aCrear);
    }

    @Async("taskExecutor")
    @Override
    public void sendNotification() {
        List<LicitacionEmail> pendientes = licitacionEmailRepository.findByEnviadoFalse();

        Map<String, List<LicitacionEmail>> pendientesPorEmail;
        boolean sinLicitaciones = false;

        if (pendientes.isEmpty()) {
            sinLicitaciones = true;
            pendientesPorEmail = new HashMap<>();
            List<String> emailsActivos = emailService.findAllActiveEmails();
            for (String email : emailsActivos) {
                pendientesPorEmail.put(email, new ArrayList<>());
            }
        } else {
            pendientesPorEmail = pendientes.stream()
                    .collect(Collectors.groupingBy(le -> le.getEmail().getDireccionEmail()));
        }

        int totalEnviadas = 0;

        try {
            for (Map.Entry<String, List<LicitacionEmail>> entry : pendientesPorEmail.entrySet()) {
                String email = entry.getKey();
                List<LicitacionEmail> registros = entry.getValue();

                List<LicitacionDTO> licitaciones = registros.stream()
                        .map(LicitacionEmail::getLicitacion)
                        .map(licitacionMapper::licitacionToLicitacionDTO)
                        .toList();

                String html;
                String subject;
                if (licitaciones.isEmpty()) {
                    html = emailTemplateService.generarLicitacionesHtml(List.of(), LocalDateTime.now());
                    subject = "Sin licitaciones ARCE - " + LocalDate.now();
                } else {
                    html = emailTemplateService.generarLicitacionesHtml(licitaciones, LocalDateTime.now());
                    subject = licitaciones.size() + " Licitaciones ARCE - " + LocalDate.now();
                }

                emailTransportService.sendHtmlEmail(List.of(email), subject, html);

                List<Integer> idsLicitaciones = registros.stream()
                        .map(LicitacionEmail::getIdLicitacion)
                        .toList();
                if (!idsLicitaciones.isEmpty()) saveSentEmails(idsLicitaciones, List.of(email));
                totalEnviadas += idsLicitaciones.size();
            }

            notificacionService.create(
                    NotificacionType.EMAIL,
                    sinLicitaciones ? "Notificación sin licitaciones" : "Envío de licitaciones ARCE",
                    true,
                    sinLicitaciones
                            ? "Notificación enviada a " + pendientesPorEmail.size() + " destinatario(s)"
                            : totalEnviadas + " licitaciones enviadas a " + pendientesPorEmail.size() + " destinatario(s)",
                    null,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            notificacionService.create(
                    NotificacionType.EMAIL,
                    "Error en envío de licitaciones ARCE - " + LocalDate.now(),
                    false,
                    e.getMessage(),
                    null,
                    LocalDateTime.now()
            );
            throw e;
        }
    }

    @Transactional
    public void saveSentEmails(List<Integer> licitacionIds, List<String> emailAddresses) {
        if (licitacionIds.isEmpty() || emailAddresses.isEmpty()) return;
        licitacionEmailRepository.updateEnviado(licitacionIds, new HashSet<>(emailAddresses));
    }
}
