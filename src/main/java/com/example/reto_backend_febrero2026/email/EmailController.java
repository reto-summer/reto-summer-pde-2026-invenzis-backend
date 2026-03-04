package com.example.reto_backend_febrero2026.email;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final IEmailService emailService;

    public EmailController(IEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllActiveEmails() {
        return ResponseEntity.ok(emailService.findAllActiveEmails());
    }

    @Operation(summary = "Obtener destinatario", description = "Devuelve un destinatario por su dirección de email.")
    @GetMapping("/{emailAddress:.+}")
    public EmailDTO getDestinationById(@PathVariable String emailAddress) {
        return emailService.findById(emailAddress);
    }

    @Operation(summary = "Crear destinatario", description = "Registra un nuevo destinatario. Body: { email }")
    @PostMapping
    public ResponseEntity<String> createDestination(@RequestBody EmailDTO body) {
        emailService.create(body.getEmail());
        return ResponseEntity.ok("Email creado exitosamente");
    }

    // controller para futuras implementaciones de la app
/*    @PutMapping("/{emailAddress:.+}")
    public ResponseEntity<EmailDTO> updateDestination(@PathVariable String emailAddress, @RequestBody EmailDTO body) {
        return ResponseEntity.ok(emailService.update(emailAddress, body.getActivo()));
    }*/

    @Operation(summary = "Desactivar destinatario", description = "Marca un destinatario como inactivo (soft delete).")
    @DeleteMapping("/{emailAddress:.+}")
    public ResponseEntity<Object> deleteDestination(@PathVariable String emailAddress) {
        emailService.deactivate(emailAddress);
        return ResponseEntity.noContent().build();
    }
}
