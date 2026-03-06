package com.example.reto_backend_febrero2026.notificacion;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final INotificacionService notificacionService;

    public NotificacionController(INotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Operation(summary = "Listar notificaciones", description = "Devuelve el resumen de todas las notificaciones.")
    @GetMapping
    public ResponseEntity<List<NotificacionResumenDTO>> getAll() {
        return ResponseEntity.ok(notificacionService.findAllResumen());
    }


    @Operation(summary = "Notificación por ID", description = "Obtiene el detalle de una notificación por su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDetalleDTO> getById(@PathVariable Integer id) {
        Optional<NotificacionDetalleDTO> notificacion = notificacionService.findById(id);
        return notificacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Notificaciones exitosas", description = "Devuelve solo las notificaciones enviadas con éxito.")
    @GetMapping("/exitosas")
    public ResponseEntity<List<NotificacionResumenDTO>> getExitosas() {
        return ResponseEntity.ok(notificacionService.findExitosas());
    }
}