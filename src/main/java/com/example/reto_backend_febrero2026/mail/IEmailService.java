package com.example.reto_backend_febrero2026.mail;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;

import java.util.List;
import java.util.Optional;

public interface IEmailService {

    List<Email> findAllActive();

    Optional<Email> findById(String emailAddress);

    Email create(String email);

    Email update(String emailAddress, Boolean activo);

    void deactivate(String emailAddress);

    List<String> findAllActiveEmails();

    void sendLicitacionesEmail(List<LicitacionItemRecord> items);
}
