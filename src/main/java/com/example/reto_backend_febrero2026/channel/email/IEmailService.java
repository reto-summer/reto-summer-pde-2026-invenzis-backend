package com.example.reto_backend_febrero2026.channel.email;

import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

import java.util.List;

public interface IEmailService {

    List<EmailDTO> findAllActive();

    EmailDTO findById(String emailAddress);

    EmailDTO create(String email);

    EmailDTO update(String emailAddress, Boolean habilitadoEnvio);

    void deactivate(String emailAddress);

    List<String> findAllActiveEmails();

    void sendLicitacionesEmail(List<LicitacionDTO> licitaciones);
}
