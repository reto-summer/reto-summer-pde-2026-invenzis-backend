package com.example.reto_backend_febrero2026.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "*")
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
        return emailService.findById(emailAddress)
                .orElseThrow(() -> new IllegalArgumentException("Email no encontrado: " + emailAddress));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public EmailDTO createDestination(@RequestBody EmailDTO body) {
        return emailService.create(body.getEmail());
    }

    // controller para futuras implementaciones de la app
/*    @PutMapping("/{emailAddress:.+}")
    public EmailDTO updateDestination(@PathVariable String emailAddress, @RequestBody EmailDTO body) {
        return emailService.update(emailAddress, body.getActivo());
    }*/

    @DeleteMapping("/{emailAddress:.+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDestination(@PathVariable String emailAddress) {
        emailService.deactivate(emailAddress);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    // Ya no se usa el HttpStatus.CREATED (201), ahora se usa HttpStatus.OK (200)
    /*@ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(IllegalStateException ex) {
        return ex.getMessage();
    }*/

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(RuntimeException ex) {
        return ex.getMessage();
    }
}
