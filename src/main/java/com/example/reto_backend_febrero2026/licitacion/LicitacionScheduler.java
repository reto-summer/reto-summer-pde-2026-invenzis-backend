package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.config.Config;
import com.example.reto_backend_febrero2026.config.IConfigService;
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
    private final ILicitacionService licitacionService;

    public LicitacionScheduler(ArceClientService arceClientService, IEmailService mailService,
                               IConfigService configService, ILicitacionService licitacionService) {
        this.arceClientService = arceClientService;
        this.mailService = mailService;
        this.configService = configService;
        this.licitacionService = licitacionService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Montevideo")
    public void getLicitacionesByConfig() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        try {
            Config config = configService.getEntityConfig();
            ArceRssFilters filters = new ArceRssFilters(config.getFamilia().getCod(), config.getSubfamilia().getCod());

            // 1. Sincronizar RSS → BD
            arceClientService.obtenerLicitaciones(filters).get();

            // 2. Obtener licitaciones no enviadas desde la BD
            List<LicitacionDTO> licitaciones = licitacionService
                    .getLicitacionesNoEnviadasByFamiliaAndSubfamilia(filters.familyCod(), filters.subFamilyCod());

            // 3. Enviar email (incluso si la lista está vacía)
            mailService.sendLicitacionesEmail(licitaciones);

            // 4. Marcar como enviadas
            licitaciones.forEach(dto -> licitacionService.updateEnviadoFlag(dto.getIdLicitacion(), true));

            log.info("Envío diario completado con {} licitaciones", licitaciones.size());
        } catch (Exception e) {
            log.error("Error en envío diario de licitaciones: {}", e.getMessage(), e);
        }
    }
}
