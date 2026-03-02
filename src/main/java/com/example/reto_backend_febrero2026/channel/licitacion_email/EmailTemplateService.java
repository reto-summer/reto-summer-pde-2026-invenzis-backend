package com.example.reto_backend_febrero2026.channel.licitacion_email;

import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailTemplateService {

    private final TemplateEngine templateEngine;
    private final Locale defaultLocale = new Locale("es","UY");

    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generarLicitacionesHtml(List<LicitacionDTO> items, LocalDateTime fecha) {
        String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Context ctx = new Context(defaultLocale);
        ctx.setVariable("items", items != null ? items : List.of());
        ctx.setVariable("fecha", fechaFormateada);
        return templateEngine.process("email/licitaciones", ctx);
    }

}
