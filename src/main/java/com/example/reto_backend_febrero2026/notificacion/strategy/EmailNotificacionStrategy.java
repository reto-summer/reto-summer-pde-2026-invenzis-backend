package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.reto_backend_febrero2026.email.IEmailService;
import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;

@Component
public class EmailNotificacionStrategy implements INotificacionStrategy {

    private final IEmailService emailService;

    public EmailNotificacionStrategy(IEmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public NotificacionType getNotificacionType() {
        return NotificacionType.EMAIL;
    }

    @Override
    public Notificacion send(String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        List<String> destinatariosActivos = emailService.findAllActiveEmails();
        String tituloConCanal = "[EMAIL] " + titulo;
        String detalleConDestino = construirDetalle(detalle, destinatariosActivos);
        return new Notificacion(tituloConCanal, exito, detalleConDestino, contenido, fechaEjecucion);
    }

    private String construirDetalle(String detalleOriginal, List<String> destinatariosActivos) {
        String prefijoDestinos = "Destinatarios activos: "
                + (destinatariosActivos.isEmpty() ? "0" : String.join(", ", destinatariosActivos));

        if (detalleOriginal == null || detalleOriginal.isBlank()) {
            return prefijoDestinos;
        }

        return detalleOriginal + " | " + prefijoDestinos;
    }
}