package com.example.reto_backend_febrero2026.scheduler;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LicitacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicitacionScheduler.class);

    private final MailService mailService;

    public LicitacionScheduler(MailService mailService) {
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void enviarResumenDiario() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        // obtener items del RSS (LicitacionService)
        List<LicitacionModel> items = List.of();
        mailService.sendLicitacionesEmail(items);
        log.info("Envío diario completado");
    }
}
