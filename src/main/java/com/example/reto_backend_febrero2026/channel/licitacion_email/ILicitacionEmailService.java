package com.example.reto_backend_febrero2026.channel.licitacion_email;

public interface ILicitacionEmailService {

    void savePendingEmails();

    void sendNotification();
}
