package com.example.reto_backend_febrero2026.channel.licitacion_email;

import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IEmailTemplateService {

    String generarLicitacionesHtml(List<LicitacionDTO> items, LocalDateTime fecha);

    String generarSinLicitacionesHtml(LocalDateTime fecha);
}
