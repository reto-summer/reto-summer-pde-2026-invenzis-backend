package com.example.reto_backend_febrero2026.service;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.to}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public MailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    private String[] parseEmailList(String emailString) {
        if (emailString == null || emailString.trim().isEmpty()) {
            log.warn("Lista de emails vacía");
            return new String[0];
        }

        return Arrays.stream(emailString.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .filter(email -> email.matches(EMAIL_REGEX))
                .toArray(String[]::new);
    }


    public void sendLicitacionesEmail(List<LicitacionModel> items) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "UY")));

        Context ctx = new Context(new Locale("es", "UY"));
        ctx.setVariable("items", items);
        ctx.setVariable("fecha", fecha);

        String htmlContent = templateEngine.process("email/licitaciones", ctx);

        // Parsear emails desde application.properties
        String[] recipients = parseEmailList(mailTo);

        if (recipients.length == 0) {
            log.error("No hay destinatarios válidos configurados en mail.to");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(recipients);
            helper.setSubject(items.size() + " Licitaciones ARCE - " + fecha);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de licitaciones enviado a {} destinatarios con {} ítems", recipients.length, items.size());
        } catch (Exception e) {
            log.error("Error al enviar email de licitaciones: {}", e.getMessage(), e);
        }
    }
}
