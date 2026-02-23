package com.example.reto_backend_febrero2026.mail.controller;

import com.example.reto_backend_febrero2026.mail.MailModel;
import com.example.reto_backend_febrero2026.mail.service.interfaces.IMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mail-destinations")
@CrossOrigin(origins = "*")
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);

    private final IMailService mailService;

    public MailController(IMailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping
    public ResponseEntity<List<MailModel>> getAllActiveDestinations() {
        return ResponseEntity.ok(mailService.findAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MailModel> getDestinationById(@PathVariable Long id) {
        Optional<MailModel> destination = mailService.findById(id);
        return destination.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDestination(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        String email = body.get("email");

        if (email == null || email.trim().isEmpty()) {
            response.put("error", "El email no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            MailModel saved = mailService.create(email);
            response.put("mensaje", "Email registrado exitosamente");
            response.put("id", saved.getId());
            response.put("email", saved.getEmail());
            response.put("fechaCreacion", saved.getFechaCreacion());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            log.error("Error al crear destino de email", e);
            response.put("error", "Error al registrar el email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDestination(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        String email = (String) body.get("email");
        Boolean activo = body.get("activo") != null ? (Boolean) body.get("activo") : null;

        try {
            MailModel updated = mailService.update(id, email, activo);
            response.put("mensaje", "Email actualizado exitosamente");
            response.put("id", updated.getId());
            response.put("email", updated.getEmail());
            response.put("activo", updated.getActivo());
            response.put("fechaActualizacion", updated.getFechaActualizacion());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                response.put("error", e.getMessage());
                return ResponseEntity.notFound().build();
            }
            log.error("Error al actualizar destino de email", e);
            response.put("error", "Error al actualizar el email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDestination(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            mailService.deactivate(id);
            response.put("mensaje", "Email eliminado exitosamente");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                response.put("error", e.getMessage());
                return ResponseEntity.notFound().build();
            }
            log.error("Error al eliminar destino de email", e);
            response.put("error", "Error al eliminar el email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/active/emails")
    public ResponseEntity<List<String>> getAllActiveEmails() {
        return ResponseEntity.ok(mailService.findAllActiveEmails());
    }
}
