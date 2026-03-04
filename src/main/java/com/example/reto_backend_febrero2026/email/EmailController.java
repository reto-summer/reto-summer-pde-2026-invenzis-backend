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

    @Operation(summary = "Listar destinatarios activos", description = "Devuelve todas las direcciones de email con activo=true.")
    @GetMapping
    public ResponseEntity<List<String>> getAllActiveEmails() {
        return ResponseEntity.ok(emailService.findAllActiveEmails());
    }

    @Operation(summary = "Destinatario por email", description = "Obtiene un destinatario por su dirección de correo.")
    @GetMapping("/{direccionEmail:.+}")
    public EmailDTO getDestinationById(@PathVariable String direccionEmail) {
        return emailService.findById(direccionEmail);
    }

    @Operation(summary = "Crear destinatario", description = "Registra una nueva dirección de email para recibir licitaciones.")
    @PostMapping
    public ResponseEntity<String> createDestination(@RequestBody EmailDTO body) {
        emailService.create(body.getDireccionEmail());
        return ResponseEntity.ok("Email creado exitosamente");
    }

    @Operation(summary = "Desactivar destinatario", description = "Desactiva un destinatario por dirección de email (soft delete).")
    @DeleteMapping("/{direccionEmail:.+}")
    public ResponseEntity<Object> deleteDestination(@PathVariable String direccionEmail) {
        emailService.deactivate(direccionEmail);
        return ResponseEntity.noContent().build();
    }
}
