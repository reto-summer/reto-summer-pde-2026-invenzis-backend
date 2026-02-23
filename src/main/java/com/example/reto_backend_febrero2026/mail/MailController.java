package com.example.reto_backend_febrero2026.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mail-destinations")
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Autowired
    private MailRepository mailRepository;

    @GetMapping
    public ResponseEntity<List<MailDestination>> getAllActiveDestinations() {
        log.info("Obteniendo todos los destinos de email activos");
        List<MailDestination> destinations = mailRepository.findByActivoTrue();
        return ResponseEntity.ok(destinations);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MailDestination> getDestinationById(@PathVariable Long id) {
        log.info("Obteniendo destino de email con ID: {}", id);
        Optional<MailDestination> destination = mailRepository.findById(id);
        return destination.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> createDestination(@RequestBody MailDestination mailDestination) {
        log.info("Creando nuevo destino de email: {}", mailDestination.getEmail());
        
        Map<String, Object> response = new HashMap<>();


        if (mailDestination.getEmail() == null || mailDestination.getEmail().trim().isEmpty()) {
            response.put("error", "El email no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        if (!isValidEmail(mailDestination.getEmail())) {
            response.put("error", "El formato del email no es válido");
            return ResponseEntity.badRequest().body(response);
        }

        if (mailRepository.existsByEmail(mailDestination.getEmail())) {
            response.put("error", "El email ya existe en la base de datos");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            mailDestination.setEmail(mailDestination.getEmail().trim().toLowerCase());
            mailDestination.setActivo(true);
            MailDestination savedDestination = mailRepository.save(mailDestination);
            
            response.put("mensaje", "Email registrado exitosamente");
            response.put("id", savedDestination.getId());
            response.put("email", savedDestination.getEmail());
            response.put("fechaCreacion", savedDestination.getFechaCreacion());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error al crear destino de email", e);
            response.put("error", "Error al registrar el email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDestination(
            @PathVariable Long id,
            @RequestBody MailDestination mailDestination) {
        log.info("Actualizando destino de email con ID: {}", id);
        
        Map<String, Object> response = new HashMap<>();

        Optional<MailDestination> existing = mailRepository.findById(id);
        if (existing.isEmpty()) {
            response.put("error", "Destino de email no encontrado");
            return ResponseEntity.notFound().build();
        }

        MailDestination destination = existing.get();

        if (mailDestination.getEmail() != null && !mailDestination.getEmail().trim().isEmpty()) {
            String newEmail = mailDestination.getEmail().trim().toLowerCase();
            
            if (!isValidEmail(newEmail)) {
                response.put("error", "El formato del email no es válido");
                return ResponseEntity.badRequest().body(response);
            }

            if (!destination.getEmail().equals(newEmail) && mailRepository.existsByEmail(newEmail)) {
                response.put("error", "El email ya existe en la base de datos");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            destination.setEmail(newEmail);
        }

        if (mailDestination.getActivo() != null) {
            destination.setActivo(mailDestination.getActivo());
        }

        try {
            MailDestination updated = mailRepository.save(destination);
            response.put("mensaje", "Email actualizado exitosamente");
            response.put("id", updated.getId());
            response.put("email", updated.getEmail());
            response.put("activo", updated.getActivo());
            response.put("fechaActualizacion", updated.getFechaActualizacion());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al actualizar destino de email", e);
            response.put("error", "Error al actualizar el email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDestination(@PathVariable Long id) {
        log.info("Eliminando destino de email con ID: {}", id);
        
        Map<String, Object> response = new HashMap<>();

        Optional<MailDestination> existing = mailRepository.findById(id);
        if (existing.isEmpty()) {
            response.put("error", "Destino de email no encontrado");
            return ResponseEntity.notFound().build();
        }

        try {
            MailDestination destination = existing.get();
            destination.setActivo(false);
            mailRepository.save(destination);
            
            response.put("mensaje", "Email eliminado exitosamente");
            response.put("id", id);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al eliminar destino de email", e);
            response.put("error", "Error al eliminar el email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/active/emails")
    public ResponseEntity<List<String>> getAllActiveEmails() {
        log.info("Obteniendo lista de emails activos");
        List<String> emails = mailRepository.findAllActiveEmails();
        return ResponseEntity.ok(emails);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}
