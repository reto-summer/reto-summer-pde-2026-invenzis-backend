package com.example.reto_backend_febrero2026.integration.servlet.service;

import com.example.reto_backend_febrero2026.audit.AuditService;
import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.dto.RssResponseDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ArceClientService {

    private final RestClient restClient;

    @Autowired
    AuditService auditService;

    @Autowired
    ILicitacionService licitacionService;

    public ArceClientService(RestClient.Builder builder) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
        this.restClient = builder
                .baseUrl("https://www.comprasestatales.gub.uy")
                .messageConverters(converters -> converters.add(converter))
                .build();
    }

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 3)
    )

    @Auditable(module = "ARCE_CLIENTE_SERVICE", action = "GET_LICITACIONES_FROM_ARCE")
    public CompletableFuture<List<LicitacionDTO>> obtenerLicitaciones(Integer familyCod, Integer subFamilyCod) {
        try {
            RssResponseDTO response = restClient.get()
                    .uri("/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/{familyCod}/sub-familia/{subFamilyCod}", familyCod, subFamilyCod)
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .body(RssResponseDTO.class);

            if (response == null || response.channel() == null || response.channel().items() == null) {
                return CompletableFuture.completedFuture(List.of());
            }

            List<LicitacionDTO> resultados = response.channel().items().stream()
                    .map(item -> {

                        LicitacionItemRecord record = new LicitacionItemRecord(
                                item.titulo(),
                                item.descripcion(),
                                item.link(),
                                item.fechaPublicacion(),
                                null,
                                familyCod,
                                subFamilyCod
                        );
                        return licitacionService.cleanSave(record);
                    })
                    .toList();

            return CompletableFuture.completedFuture(resultados);

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con ARCE RSS: " + e.getMessage(), e);
        }
    }

    @Recover
    public CompletableFuture<List<LicitacionDTO>> recover(Exception e, Integer familyCod, Integer subFamilyCod) {
        String traceId = org.slf4j.MDC.get("traceId");
        String errorMessage = "FALTA: Se agotaron los 5 reintentos para familia " + familyCod;

        System.err.println(errorMessage + " | Detalle: " + e.getMessage());

        auditService.saveAuditLog(
                traceId, "ARCE_CLIENT_SERVICE", "RECOVER_MODE",
                errorMessage, e.getMessage(), "FATAL"
        );

        return CompletableFuture.completedFuture(List.of());
    }
}