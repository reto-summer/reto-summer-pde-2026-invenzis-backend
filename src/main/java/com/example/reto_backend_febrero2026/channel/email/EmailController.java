package com.example.reto_backend_febrero2026.channel.email;

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

    @GetMapping("/{direccionEmail:.+}")
    public EmailDTO getDestinationById(@PathVariable String direccionEmail) {
        return emailService.findById(direccionEmail);
    }

    @PostMapping
    public ResponseEntity<String> createDestination(@RequestBody EmailDTO body) {
        emailService.create(body.getEmailAddress());
        return ResponseEntity.ok("Email creado exitosamente");
    }

    // controller para futuras implementaciones de la app
/*    @PutMapping("/{direccionEmail:.+}")
    public ResponseEntity<EmailDTO> updateDestination(@PathVariable String direccionEmail, @RequestBody EmailDTO body) {
        return ResponseEntity.ok(emailService.update(direccionEmail, body.getActivo()));
    }*/

    @DeleteMapping("/{direccionEmail:.+}")
    public ResponseEntity<Object> deleteDestination(@PathVariable String direccionEmail) {
        emailService.deactivate(direccionEmail);
        return ResponseEntity.noContent().build();
    }
}
