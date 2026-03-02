package com.example.reto_backend_febrero2026.channel.email;

import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    private final Validator validator;

    public EmailValidator(Validator validator) {
        this.validator = validator;
    }

    public boolean isValid(String direccionEmail) {
        if(direccionEmail == null || direccionEmail.isBlank()) {
            return false;
        }

        return  validator.validateValue(EmailWrapper.class, "email", direccionEmail).isEmpty();
    }

    public void validateOrThrow(String direccionEmail) {
       if (!isValid(direccionEmail)) {
           throw new IllegalArgumentException("El formate de email no es valido: " + direccionEmail);
       }
    }

    public String normalize(String direccionEmail) {
        return direccionEmail != null ? direccionEmail.trim().toLowerCase() : null;
    }


    private static class EmailWrapper {
        @Email(message = "Email inválido")
        private String email;
    }
}