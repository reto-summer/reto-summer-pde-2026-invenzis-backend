package com.example.reto_backend_febrero2026.integration.servlet.service;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.repository.implementation.FamiliaRepository;
import com.example.reto_backend_febrero2026.audit.AuditService;
import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.dto.RssResponseDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.example.reto_backend_febrero2026.subfamilia.repository.implementation.SubfamiliaRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArceClientService {

    private final RestClient restClient;
    private final ILicitacionRepository licitacionRepository;
    private final FamiliaRepository familiaRepository;
    private final SubfamiliaRepository subfamiliaRepository;

    private static final Integer FAMILIA_COD = 3;
    private static final Integer SUBFAMILIA_COD = 10;

    public ArceClientService(RestClient.Builder builder,
                             ILicitacionRepository licitacionRepository,
                             FamiliaRepository familiaRepository,
                             SubfamiliaRepository subfamiliaRepository) {

        this.licitacionRepository = licitacionRepository;
        this.familiaRepository = familiaRepository;
        this.subfamiliaRepository = subfamiliaRepository;

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2XmlHttpMessageConverter converter =
                new MappingJackson2XmlHttpMessageConverter(xmlMapper);

        this.restClient = builder
                .baseUrl("https://www.comprasestatales.gub.uy")
                .messageConverters(converters -> converters.add(converter))
                .build();
    }

    public void obtenerLicitaciones() {

        RssResponseDTO response = restClient.get()
                .uri("/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/3/sub-familia/10")
                .accept(MediaType.APPLICATION_XML,
                        MediaType.TEXT_XML,
                        MediaType.valueOf("application/rss+xml"))
                .retrieve()
                .body(RssResponseDTO.class);

        if (response == null || response.channel() == null) {
            return;
        }

        // Familia guarda si no existe
        FamiliaModel familia;
    @Async
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 3)
    )
    @Auditable(module = "ARCE_CLIENTE_SERVICE",action = "GET_LICITACIONES_FROM_ARCE")
    public List<LicitacionItemRecord> obtenerLicitaciones() {
        try {
            familia = familiaRepository.findById(FAMILIA_COD);
        } catch (Exception e) {
            familia = new FamiliaModel(FAMILIA_COD, "Familia " + FAMILIA_COD);
            familiaRepository.save(familia);
        }

        //Subfamilia guarda si no existe
        SubfamiliaModel subfamilia;
        try {
            subfamilia = subfamiliaRepository.findById(FAMILIA_COD, SUBFAMILIA_COD);
        } catch (Exception e) {
            subfamilia = new SubfamiliaModel(FAMILIA_COD, SUBFAMILIA_COD, "Subfamilia " + SUBFAMILIA_COD);
            subfamiliaRepository.save(subfamilia);
        }

        DateTimeFormatter pubFormatter =
                DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        for (LicitacionItemRecord item : response.channel().items()) {

            try {

                String descripcionLimpia = limpiarHtml(item.description());

                OffsetDateTime fechaPublicacion =
                        OffsetDateTime.parse(item.fechaPublicacion(), pubFormatter);

                LocalDateTime fechaCierre =
                        extraerFechaCierre(descripcionLimpia);

                LicitacionModel licitacion = new LicitacionModel();
                licitacion.setTitle(item.titulo());
                licitacion.setDescription(descripcionLimpia);
                licitacion.setFechaPublicacion(fechaPublicacion);
                licitacion.setFechaCierre(fechaCierre);
                licitacion.setLink(item.link());

                // RELACIONES
                licitacion.setFamilia(familia);
                licitacion.setSubfamilia(subfamilia);

                licitacionRepository.save(licitacion);

            } catch (Exception e) {
                System.err.println("Error procesando item: " + item.titulo());
                e.printStackTrace();
            }
        }
    }

    private String limpiarHtml(String texto) {
        return texto
                .replaceAll("<br\\s*/?>", "\n")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&sol;", "/")
                .replaceAll("<.*?>", "")
                .trim();
    }

    private LocalDateTime extraerFechaCierre(String descripcion) {

        Pattern pattern = Pattern.compile(
                "Recepción de ofertas hasta:\\s*(\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2})"
        );

        Matcher matcher = pattern.matcher(descripcion);

        if (matcher.find()) {
            String fechaStr = matcher.group(1);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            return LocalDateTime.parse(fechaStr, formatter);
        }

        return null;
    }
}

    @Recover
    public List<LicitacionItemRecord> recover(Exception e) {
        String traceId = org.slf4j.MDC.get("traceId");
        String errorMessage = "FALTA: Se agotaron los 5 reintentos. El Servicio RSS de ARCE no responde";
        System.err.println(errorMessage + "Detalle: " + e.getMessage());

        auditService.saveAuditLog(
                traceId, "ARCE_CLIENT_SERVICE","RECOVER_MODE",
                errorMessage, e.getMessage(), "FATAL"
        );
        return List.of();
    }
}
