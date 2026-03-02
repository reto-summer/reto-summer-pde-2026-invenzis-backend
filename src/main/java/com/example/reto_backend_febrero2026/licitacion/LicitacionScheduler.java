package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.config.Config;
import com.example.reto_backend_febrero2026.config.IConfigService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import com.example.reto_backend_febrero2026.email.IEmailService;
import com.example.reto_backend_febrero2026.channel.licitacion_email.ILicitacionEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LicitacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicitacionScheduler.class);

    private final ArceClientService arceClientService;
    private final IEmailService emailService;
    private final IConfigService configService;
    private final ILicitacionService licitacionService;
    private final ILicitacionEmailService licitacionEmailService;
    private final LicitacionMapper licitacionMapper;

    public LicitacionScheduler(ArceClientService arceClientService, IEmailService emailService,
                               IConfigService configService, ILicitacionService licitacionService,
                               ILicitacionEmailService licitacionEmailService,
                               LicitacionMapper licitacionMapper) {
        this.arceClientService = arceClientService;
        this.emailService = emailService;
        this.configService = configService;
        this.licitacionService = licitacionService;
        this.licitacionEmailService = licitacionEmailService;
        this.licitacionMapper = licitacionMapper;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Montevideo")
    public void getLicitacionesByConfig() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        try {
            Config config = configService.getEntityConfig();
            ArceRssFilters filters = new ArceRssFilters(config.getFamilia().getCod(), config.getSubfamilia().getCod());

            // 1. Sincronizar RSS → BD
            List<LicitacionDTO> nuevasLicitaciones = arceClientService.obtenerLicitaciones(filters).get();

            for (LicitacionDTO licitacionDTO : nuevasLicitaciones) {
                LicitacionItemRecord licitacion = licitacionMapper.DTOtoLicitacionItemRecordItem(licitacionDTO);
                licitacionService.save(licitacion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Montevideo")
    public void ejecutarProcesoNotificaciones() {
        licitacionEmailService.savePendingEmails();

        licitacionEmailService.sendNotification();
    }
}
