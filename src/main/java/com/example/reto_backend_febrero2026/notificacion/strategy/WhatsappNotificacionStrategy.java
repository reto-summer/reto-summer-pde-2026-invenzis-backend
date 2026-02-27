package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;

@Component
public class WhatsappNotificacionStrategy implements INotificacionStrategy {

    @Override
    public NotificacionType getNotificacionType() {
        return NotificacionType.WHATSAPP;
    }

    @Override
    public Notificacion send(String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        String tituloConCanal = "[WHATSAPP] " + titulo;
        return new Notificacion(tituloConCanal, exito, detalle, contenido, fechaEjecucion);
    }
}