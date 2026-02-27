package com.example.reto_backend_febrero2026.email;

import org.springframework.http.HttpStatus;
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

    @GetMapping("/{emailAddress:.+}")
    public ResponseEntity<EmailDTO> getDestinationById(@PathVariable String emailAddress) {
        return emailService.findById(emailAddress)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{emailAddress:.+}")
    public ResponseEntity<Void> deleteDestination(@PathVariable String emailAddress) {
        emailService.deactivate(emailAddress);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
