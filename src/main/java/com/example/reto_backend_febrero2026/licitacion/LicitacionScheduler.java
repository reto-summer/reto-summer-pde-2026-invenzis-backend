package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.config.Config;
import com.example.reto_backend_febrero2026.config.IConfigService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
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
    private final IConfigService configService;

    public LicitacionScheduler(ArceClientService arceClientService, IEmailService mailService, IConfigService configService) {
        this.arceClientService = arceClientService;
        this.mailService = mailService;
        this.configService = configService;
    }

    @Scheduled(cron = "0 * * * * *", zone = "America/Montevideo")
    public void getLicitacionesByConfig() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        try {
            Config config = configService.getConfig();
            ArceRssFilters filters = new ArceRssFilters(config.getFamilia().getCod(), config.getSubfamilia().getCod());

            List<LicitacionDTO> licitaciones = arceClientService
                    .obtenerLicitaciones(filters)
                    .get();

            List<LicitacionItemRecord> items = licitaciones.stream()
                    .map(dto -> new LicitacionItemRecord(
                            dto.getTitulo(),
                            dto.getDescripcion(),
                            dto.getLink(),
                            dto.getFechaPublicacion() != null ? dto.getFechaPublicacion().toString() : null,
                            dto.getFechaCierre() != null ? dto.getFechaCierre().toString() : null,
                            filters.familyCod(),
                            filters.subFamilyCod(),
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
