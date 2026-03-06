package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.channel.licitacion_email.ILicitacionEmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LicitacionScheduler {

    private final ILicitacionSyncService syncService;
    private final ILicitacionEmailService licitacionEmailService;

    public LicitacionScheduler(
            ILicitacionSyncService syncService,
            ILicitacionEmailService licitacionEmailService) {
        this.syncService = syncService;
        this.licitacionEmailService = licitacionEmailService;
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "America/Montevideo")
    public void executeDailyTask() {
        syncService.getLicitacionesByConfig();
        licitacionEmailService.savePendingEmails();
        licitacionEmailService.sendNotification();
    }
}
