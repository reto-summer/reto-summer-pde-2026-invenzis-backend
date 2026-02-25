package com.example.reto_backend_febrero2026.mail;

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
@RequestMapping("/email-destinations")
@CrossOrigin(origins = "*")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final IEmailService emailService;

    public EmailController(IEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<Email>> getAllActiveDestinations() {
        return ResponseEntity.ok(emailService.findAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Email> getDestinationById(@PathVariable Integer id) {
        Optional<Email> destination = emailService.findById(id);
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
            Email saved = emailService.create(email);
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
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        String email = (String) body.get("email");
        Boolean activo = body.get("activo") != null ? (Boolean) body.get("activo") : null;

        try {
            Email updated = emailService.update(id, email, activo);
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
    public ResponseEntity<Map<String, Object>> deleteDestination(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            emailService.deactivate(id);
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
        return ResponseEntity.ok(emailService.findAllActiveEmails());
    }
}
