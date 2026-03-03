package com.example.reto_backend_febrero2026.channel.licitacion_email;

import com.example.reto_backend_febrero2026.channel.IChannel;
import com.example.reto_backend_febrero2026.email.Email;
import com.example.reto_backend_febrero2026.email.EmailDTO;
import com.example.reto_backend_febrero2026.email.EmailMapper;
import com.example.reto_backend_febrero2026.email.IEmailService;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionMapper;
import com.example.reto_backend_febrero2026.notificacion.INotificacionService;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicitacionEmailService implements ILicitacionEmailService, IChannel {

    private final ILicitacionEmailRepository licitacionEmailRepository;
    private final ILicitacionService licitacionService;
    private final IEmailService emailService;
    private final EmailMapper emailMapper;
    private final LicitacionMapper licitacionMapper;
    private final IEmailTemplateService emailTemplateService;
    private final IEmailTransportService emailTransportService;
    private final INotificacionService notificacionService;

    public LicitacionEmailService(ILicitacionEmailRepository licitacionEmailRepository, ILicitacionService licitacionService,
                                  IEmailService emailService, EmailMapper emailMapper, LicitacionMapper licitacionMapper,
                                  IEmailTransportService emailTransportService, IEmailTemplateService emailTemplateService,
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

    @Override
    public void sendNotification() {

        List<LicitacionEmail> pendientes = getPendientes();

        if (pendientes.isEmpty()) {
            enviarNotificacionSinLicitaciones();
            return;
        }

        Map<String, List<LicitacionEmail>> pendientesPorEmail =
                pendientes.stream()
                        .collect(Collectors.groupingBy(
                                le -> le.getEmail().getDireccionEmail()));

        int totalEnviadas = 0;

        try {
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

                String html = emailTemplateService.generarLicitacionesHtml(licitaciones, LocalDateTime.now());
                String subject = licitaciones.size() + " Licitaciones ARCE - " + LocalDate.now();

                emailTransportService.sendHtmlEmail(List.of(email), subject, html);

                List<Integer> idsLicitaciones = registros.stream()
                        .map(LicitacionEmail::getIdLicitacion)
                        .toList();

                saveSentEmails(idsLicitaciones, List.of(email));
                totalEnviadas += licitaciones.size();
            }

            notificacionService.create(
                    NotificacionType.EMAIL,
                    "Envío de licitaciones ARCE - " + LocalDate.now(),
                    true,
                    totalEnviadas + " licitaciones enviadas a " + pendientesPorEmail.size() + " destinatario(s)",
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

        HashSet<String> direcciones = new HashSet<>(emailAddresses);

        licitacionEmailRepository.updateEnviado(licitacionIds, direcciones);
    }

    @Override
    @Transactional
    public void savePendingEmails() {
        // 1. Traemos licitaciones de hoy (o los últimos 2 días para dar margen)
        List<LicitacionDTO> licitacionesDTO = getLicitaciones();
        List<EmailDTO> emailsDTO = emailService.findAllActive();

        if (licitacionesDTO.isEmpty() || emailsDTO.isEmpty()) return;

        // 2. Buscamos qué combinaciones YA existen para no duplicar
        Set<String> existingKeys = licitacionEmailRepository.findByLicitacionesAndEmails(
                getLicitacionIds(licitacionesDTO),
                getEmailAddresses(emailsDTO)
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

    private List<LicitacionDTO> getLicitaciones() {
        return licitacionService.findByFilters(null, null, LocalDate.now(), null, null, null);
    }

    public List<LicitacionEmail> getPendientes() {
        return licitacionEmailRepository.findByEnviadoFalse();
    }

    private Set<String> getEmailAddresses(List<EmailDTO> emailsDTO) {
        return emailsDTO.stream()
                .map(EmailDTO::getDireccionEmail)
                .collect(Collectors.toSet());
    }

    private List<Integer> getLicitacionIds(List<LicitacionDTO> licitacionesDTO) {
        return licitacionesDTO.stream()
                .map(LicitacionDTO::getIdLicitacion)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Licitacion licitacion, Email email) {
        LicitacionEmail licEm = new LicitacionEmail(licitacion, email);
        licitacionEmailRepository.save(licEm);
    }

    private void enviarNotificacionSinLicitaciones() {
        List<String> emailsActivos = emailService.findAllActiveEmails();

        if (emailsActivos.isEmpty()) return;

        try {
            String html = emailTemplateService.generarSinLicitacionesHtml(LocalDateTime.now());
            String subject = "Sin licitaciones ARCE - " + LocalDate.now();

            emailTransportService.sendHtmlEmail(emailsActivos, subject, html);

            notificacionService.create(
                    NotificacionType.EMAIL,
                    "Notificación de sin licitaciones ARCE - " + LocalDate.now(),
                    true,
                    "Notificación enviada a " + emailsActivos.size() + " destinatario(s)",
                    null,
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            notificacionService.create(
                    NotificacionType.EMAIL,
                    "Error en envío de notificación sin licitaciones - " + LocalDate.now(),
                    false,
                    e.getMessage(),
                    null,
                    LocalDateTime.now()
            );
            throw e;
        }
    }
}
