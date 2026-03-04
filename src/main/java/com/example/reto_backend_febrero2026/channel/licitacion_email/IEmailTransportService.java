package com.example.reto_backend_febrero2026.channel.licitacion_email;

import java.util.List;

public interface IEmailTransportService {

    void sendHtmlEmail(List<String> recipients, String subject, String htmlContent);
}
