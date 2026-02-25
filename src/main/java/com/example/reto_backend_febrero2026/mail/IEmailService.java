package com.example.reto_backend_febrero2026.mail;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;

import java.util.List;
import java.util.Optional;

public interface IEmailService {

    List<Email> findAllActive();

    Optional<Email> findById(Integer id);

    Email create(String email);

    Email update(Integer id, String email, Boolean activo);

    void deactivate(Integer id);

    List<String> findAllActiveEmails();

    void sendLicitacionesEmail(List<LicitacionItemRecord> items);
}
