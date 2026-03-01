package com.example.reto_backend_febrero2026.channel.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EmailRecipientResolver {

    private final IEmailRepository emailRepository;
    private final EmailValidator emailValidator;
    private final String mailTo;

    public EmailRecipientResolver(IEmailRepository emailRepository,
                                  EmailValidator emailValidator,
                                  @Value("${mail.to}") String mailTo) {
        this.emailRepository = emailRepository;
        this.emailValidator = emailValidator;
        this.mailTo = mailTo;
    }

    public String[] resolve(){
        List<String> activeEmails = emailRepository.findAllActiveEmails();

        if(!activeEmails.isEmpty()){
            return activeEmails.toArray(String[]::new);
        }

        return Arrays.stream(mailTo.split(","))
                .map(emailValidator::normalize)
                .filter(emailValidator::isValid)
                .toArray(String[]::new);
    }
}
