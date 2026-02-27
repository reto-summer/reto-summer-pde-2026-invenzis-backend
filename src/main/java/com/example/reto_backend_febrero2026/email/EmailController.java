package com.example.reto_backend_febrero2026.email;

import jakarta.persistence.EntityNotFoundException;
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
    public List<String> getAllActiveEmails() {
        return emailService.findAllActiveEmails();
    }

    @GetMapping("/{emailAddress:.+}")
    public EmailDTO getDestinationById(@PathVariable String emailAddress) {
        return emailService.findById(emailAddress);
    }

    @PostMapping
    public EmailDTO createDestination(@RequestBody EmailDTO body) {
        return emailService.create(body.getEmail());
    }

    // controller para futuras implementaciones de la app
/*    @PutMapping("/{emailAddress:.+}")
    public EmailDTO updateDestination(@PathVariable String emailAddress, @RequestBody EmailDTO body) {
        return emailService.update(emailAddress, body.getActivo());
    }*/

    @DeleteMapping("/{emailAddress:.+}")
    public void deleteDestination(@PathVariable String emailAddress) {
        emailService.deactivate(emailAddress);
    }
}
