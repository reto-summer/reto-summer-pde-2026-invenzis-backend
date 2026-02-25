package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.mail.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LicitacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicitacionScheduler.class);

    private final ArceClientService arceClientService;
    private final IEmailService mailService;

    public LicitacionScheduler(ArceClientService arceClientService, IEmailService mailService) {
        this.arceClientService = arceClientService;
        this.mailService = mailService;
    }

    /*
    @Scheduled(cron = "0 * * * * *")
    public void enviarResumenDiario() {
        log.info("Iniciando envío diario de licitaciones ARCE");
        List<LicitacionItemRecord> items = arceClientService.obtenerLicitaciones();
        mailService.sendLicitacionesEmail(items);
        log.info("Envío diario completado");
    }
    */

}
