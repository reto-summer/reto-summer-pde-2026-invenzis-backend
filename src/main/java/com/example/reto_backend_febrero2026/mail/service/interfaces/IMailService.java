package com.example.reto_backend_febrero2026.mail.service.interfaces;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.mail.MailModel;

import java.util.List;
import java.util.Optional;

public interface IMailService {

    List<MailModel> findAllActive();

    Optional<MailModel> findById(Long id);

    MailModel create(String email);

    MailModel update(Long id, String email, Boolean activo);

    void deactivate(Long id);

    List<String> findAllActiveEmails();

    void sendLicitacionesEmail(List<LicitacionItemRecord> items);
}
