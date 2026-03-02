package com.example.reto_backend_febrero2026.notificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.reto_backend_febrero2026.notificacion.strategy.INotificacionStrategy;
import com.example.reto_backend_febrero2026.notificacion.strategy.NotificacionStrategyResolver;

@Service
public class NotificacionService implements INotificacionService {

    private final INotificacionRepository notificacionRepository;
    private final NotificacionStrategyResolver strategyResolver;

    public NotificacionService(INotificacionRepository notificacionRepository,
                               NotificacionStrategyResolver strategyResolver) {
        this.notificacionRepository = notificacionRepository;
        this.strategyResolver = strategyResolver;
    }

    @Override
    public Notificacion create(NotificacionType canal, String titulo, boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        INotificacionStrategy strategy = strategyResolver.resolve(canal);
        Notificacion notificacion = strategy.send(titulo, exito, detalle, contenido, fechaEjecucion);
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