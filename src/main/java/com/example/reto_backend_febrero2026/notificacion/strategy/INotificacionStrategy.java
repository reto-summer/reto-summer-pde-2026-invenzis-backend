package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.time.LocalDateTime;

import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;

public interface INotificacionStrategy {

    NotificacionType getNotificacionType();

    Notificacion send(String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion);
}