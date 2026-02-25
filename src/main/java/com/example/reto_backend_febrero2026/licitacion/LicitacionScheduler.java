package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.config.Config;
import com.example.reto_backend_febrero2026.config.IConfigService;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LicitacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(LicitacionScheduler.class);

    private final ArceClientService arceClientService;
    private final MailService mailService;
    private final IConfigService configService;

    public LicitacionScheduler(ArceClientService arceClientService, MailService mailService, IConfigService configService) {
        this.arceClientService = arceClientService;
        this.mailService = mailService;
        this.configService = configService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Montevideo")
    public void getLicitacionesByConfig()
    {
        Config config = this.configService.getConfig();

        System.out.println(config.getFamilia().getCod());
        System.out.println(config.getSubfamilia().getCod());
        this.arceClientService.obtenerLicitaciones(3, 10);
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
