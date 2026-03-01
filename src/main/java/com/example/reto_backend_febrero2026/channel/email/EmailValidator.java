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

    public boolean isValid(String emailAddress) {
        if(emailAddress == null || emailAddress.isBlank()) {
            return false;
        }

        return  validator.validateValue(EmailWrapper.class, "email", emailAddress).isEmpty();
    }

    public void validateOrThrow(String emailAddress) {
       if (!isValid(emailAddress)) {
           throw new IllegalArgumentException("El formate de email no es valido: " + emailAddress);
       }
    }

    public String normalize(String emailAddress) {
        return emailAddress != null ? emailAddress.trim().toLowerCase() : null;
    }


    private static class EmailWrapper {
        @Email(message = "Email inválido")
        private String email;
    }
}