package com.example.reto_backend_febrero2026.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        return emailService.findById(emailAddress)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado: " + emailAddress));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String createDestination(@RequestBody EmailDTO body) {
        emailService.create(body.getEmail());
        return "Email creado exitosamente";
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

}
