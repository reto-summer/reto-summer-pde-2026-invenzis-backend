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
@RequestMapping("/mail")
@CrossOrigin(origins = "*")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final IEmailService emailService;

    public EmailController(IEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("")
    public ResponseEntity<List<Email>> getAllActiveDestinations() {
        return ResponseEntity.ok(emailService.findAllActive());
    }

    @GetMapping("/{emailAddress}")
    public ResponseEntity<Email> getDestinationById(@PathVariable String emailAddress) {
        Optional<Email> destination = emailService.findById(emailAddress);
        return destination.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
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
            response.put("emailAddress", saved.getEmailAddress());
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

    @PutMapping("/update/{emailAddress}")
    public ResponseEntity<Map<String, Object>> updateDestination(
            @PathVariable String emailAddress,
            @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        Boolean activo = body.get("activo") != null ? (Boolean) body.get("activo") : null;

        try {
            Email updated = emailService.update(emailAddress, activo);
            response.put("mensaje", "Email actualizado exitosamente");
            response.put("emailAddress", updated.getEmailAddress());
            response.put("activo", updated.getActivo());
            response.put("fechaActualizacion", updated.getFechaActualizacion());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
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

    @DeleteMapping("/delete/{emailAddress}")
    public ResponseEntity<Map<String, Object>> deleteDestination(@PathVariable String emailAddress) {
        Map<String, Object> response = new HashMap<>();

        try {
            emailService.deactivate(emailAddress);
            response.put("mensaje", "Email eliminado exitosamente");
            response.put("emailAddress", emailAddress);
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
