package com.example.reto_backend_febrero2026.notificacion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notificacion")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final INotificacionService notificacionService;

    public NotificacionController(INotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResumenDTO>> getAll() {
        return ResponseEntity.ok(notificacionService.findAllResumen());
    }

    @GetMapping("/exitosas")
    public ResponseEntity<List<NotificacionResumenDTO>> getExitosas() {
        return ResponseEntity.ok(notificacionService.findExitosas());
    }

    @GetMapping("/fallidas")
    public ResponseEntity<List<NotificacionResumenDTO>> getFallidas() {
        return ResponseEntity.ok(notificacionService.findFallidas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDetalleDTO> getById(@PathVariable Integer id) {
        Optional<NotificacionDetalleDTO> notificacion = notificacionService.findById(id);
        return notificacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
