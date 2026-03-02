package com.example.reto_backend_febrero2026.channel.email;

import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

import java.util.List;

public interface IEmailService {

    List<EmailDTO> findAllActive();

    EmailDTO findById(String direccionEmail);

    EmailDTO create(String email);

    EmailDTO update(String direccionEmail, Boolean habilitadoEnvio);

    void deactivate(String direccionEmail);

    List<String> findAllActiveEmails();

    void sendNotification();

}
