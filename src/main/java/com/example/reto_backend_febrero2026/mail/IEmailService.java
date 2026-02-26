package com.example.reto_backend_febrero2026.mail;

import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

import java.util.List;
import java.util.Optional;

public interface IEmailService {

    List<EmailDTO> findAllActive();

    Optional<EmailDTO> findById(String emailAddress);

    EmailDTO create(String email);

    EmailDTO update(String emailAddress, Boolean activo);

    void delete(String emailAddress);

    List<String> findAllActiveEmails();

    void sendLicitacionesEmail(List<LicitacionDTO> licitaciones);
}
