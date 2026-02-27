package com.example.reto_backend_febrero2026.notificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface INotificacionService {

    Notificacion create(NotificacionType type, String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion);

    List<NotificacionResumenDTO> findAllResumen();

    Optional<NotificacionDetalleDTO> findById(Integer id);

    List<NotificacionResumenDTO> findExitosas();

    List<NotificacionResumenDTO> findFallidas();
}
