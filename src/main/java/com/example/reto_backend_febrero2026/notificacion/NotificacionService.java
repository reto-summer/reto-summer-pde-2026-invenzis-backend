package com.example.reto_backend_febrero2026.notificacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionService implements INotificacionService {

    @Autowired
    private INotificacionRepository notificacionRepository;

    @Override
    public Notificacion create(String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        Notificacion notificacion = new Notificacion(titulo, exito, detalle, contenido, fechaEjecucion);
        return notificacionRepository.save(notificacion);
    }

    @Override
    public List<NotificacionResumenDTO> findAllResumen() {
        return notificacionRepository.findAll()
                .stream()
                .map(n -> new NotificacionResumenDTO(n.getId(), n.getTitulo(), n.getExito(), n.getFechaEjecucion()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NotificacionDetalleDTO> findById(Integer id) {
        return notificacionRepository.findById(id)
                .map(n -> new NotificacionDetalleDTO(
                        n.getId(),
                        n.getTitulo(),
                        n.getExito(),
                        n.getDetalle(),
                        n.getContenido(),
                        n.getFechaEjecucion()
                ));
    }

    @Override
    public List<NotificacionResumenDTO> findExitosas() {
        return notificacionRepository.findByExitoTrue()
                .stream()
                .map(n -> new NotificacionResumenDTO(n.getId(), n.getTitulo(), n.getExito(), n.getFechaEjecucion()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificacionResumenDTO> findFallidas() {
        return notificacionRepository.findByExitoFalse()
                .stream()
                .map(n -> new NotificacionResumenDTO(n.getId(), n.getTitulo(), n.getExito(), n.getFechaEjecucion()))
                .collect(Collectors.toList());
    }
}
