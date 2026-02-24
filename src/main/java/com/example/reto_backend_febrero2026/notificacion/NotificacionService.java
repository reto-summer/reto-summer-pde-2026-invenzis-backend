package com.example.reto_backend_febrero2026.notificacion;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionService implements INotificacionService {

    private final INotificacionRepository notificacionRepository;

    public NotificacionService(INotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public Notificacion create(Integer id, String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        Notificacion notificacion = new Notificacion(id, titulo, exito, detalle, contenido, fechaEjecucion);
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
    public Integer getNextId() {
        List<Notificacion> all = notificacionRepository.findAll();
        if (all.isEmpty()) {
            return 1;
        }
        return all.stream()
                .map(Notificacion::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }
}