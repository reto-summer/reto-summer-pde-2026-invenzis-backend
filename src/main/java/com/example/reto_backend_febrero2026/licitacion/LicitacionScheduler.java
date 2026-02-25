package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.mail.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LicitacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicitacionScheduler.class);

    private final ArceClientService arceClientService;
    private final IEmailService mailService;

    public LicitacionScheduler(ArceClientService arceClientService, IEmailService mailService) {
        this.arceClientService = arceClientService;
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 */2 * * * *")
    public void enviarResumenDiario() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        try {
            int familyCod = 3;
            int subFamilyCod = 10;

            List<LicitacionDTO> licitaciones = arceClientService
                    .obtenerLicitaciones(familyCod, subFamilyCod)
                    .get();

            List<LicitacionItemRecord> items = licitaciones.stream()
                    .map(dto -> new LicitacionItemRecord(
                            dto.getTitulo(),
                            dto.getDescripcion(),
                            dto.getLink(),
                            dto.getFechaPublicacion() != null ? dto.getFechaPublicacion().toString() : null,
                            dto.getFechaCierre() != null ? dto.getFechaCierre().toString() : null,
                            familyCod,
                            subFamilyCod,
                            dto.getIdLicitacion()
                    ))
                    .toList();

            mailService.sendLicitacionesEmail(items);
            log.info("Envío diario completado con {} licitaciones", items.size());
        } catch (Exception e) {
            log.error("Error en envío diario de licitaciones: {}", e.getMessage(), e);
        }
    }
}
