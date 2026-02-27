package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import com.example.reto_backend_febrero2026.notificacion.whatsapp.IWhatsappMetaService;

@Component
public class WhatsappNotificacionStrategy implements INotificacionStrategy {

    private final IWhatsappMetaService whatsappMetaService;

    public WhatsappNotificacionStrategy(IWhatsappMetaService whatsappMetaService) {
        this.whatsappMetaService = whatsappMetaService;
    }

    @Override
    public NotificacionType getNotificacionType() {
        return NotificacionType.WHATSAPP;
    }

    @Override
    public Notificacion send(String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        String tituloConCanal = "[WHATSAPP] " + titulo;

        try {
            String message = buildWhatsappMessage(titulo, exito, detalle, contenido);
            whatsappMetaService.sendTextMessage(message);
            return new Notificacion(tituloConCanal, exito, appendDetail(detalle, "Envío WhatsApp Meta OK"), contenido, fechaEjecucion);
        } catch (Exception ex) {
            return new Notificacion(tituloConCanal, false, appendDetail(detalle, "Error WhatsApp Meta: " + ex.getMessage()), contenido, fechaEjecucion);
        }
    }

    private String buildWhatsappMessage(String titulo, boolean exito, String detalle, String contenido) {
        return "Notificación: " + titulo + "\n"
                + "Estado: " + (exito ? "EXITOSA" : "FALLIDA") + "\n"
                + "Detalle: " + safeValue(detalle) + "\n"
                + "Contenido: " + safeValue(contenido);
    }

    private String appendDetail(String baseDetail, String suffix) {
        if (baseDetail == null || baseDetail.isBlank()) {
            return suffix;
        }
        return baseDetail + " | " + suffix;
    }

    private String safeValue(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}