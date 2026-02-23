package com.example.reto_backend_febrero2026.licitacion.scheduler;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LicitacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicitacionScheduler.class);

    private final ArceClientService arceClientService;
    private final MailService mailService;

    public LicitacionScheduler(ArceClientService arceClientService, MailService mailService) {
        this.arceClientService = arceClientService;
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void enviarResumenDiario() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        List<LicitacionItemRecord> items = arceClientService.obtenerLicitaciones();
        mailService.sendLicitacionesEmail(items);
        log.info("Envío diario completado");
    }
}
