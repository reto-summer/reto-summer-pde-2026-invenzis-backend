package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;

@Component
public class EmailNotificacionStrategy implements INotificacionStrategy {

    @Override
    public NotificacionType getNotificacionType() {
        return NotificacionType.EMAIL;
    }

    @Override
    public Notificacion send(String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        String tituloConCanal = "[EMAIL] " + titulo;
        return new Notificacion(tituloConCanal, exito, detalle, contenido, fechaEjecucion);
    }
}