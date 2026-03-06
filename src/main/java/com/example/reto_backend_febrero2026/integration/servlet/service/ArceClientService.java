package com.example.reto_backend_febrero2026.integration.servlet.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.reto_backend_febrero2026.audit.IAuditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.dto.RssResponseDTO;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssUrlStrategyResolver;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Service
public class ArceClientService {

    private static final String ARCE_BASE_URL = "https://www.comprasestatales.gub.uy";

    private final RestClient restClient;
    private final ArceRssUrlStrategyResolver urlStrategyResolver;

    @Value("${arce.rss.default.family-cod:0}")
    private Integer defaultFamilyCod;

    @Value("${arce.rss.default.subfamily-cod:0}")
    private Integer defaultSubFamilyCod;

    private final IAuditService auditService;

    private final ILicitacionService licitacionService;


    public ArceClientService(RestClient.Builder builder, ArceRssUrlStrategyResolver urlStrategyResolver, IAuditService auditService, ILicitacionService licitacionService) {
        this.urlStrategyResolver = urlStrategyResolver;
        this.auditService = auditService;
        this.licitacionService = licitacionService;
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
        this.restClient = builder
                .baseUrl(ARCE_BASE_URL)
                .messageConverters(converters -> converters.add(converter))
                .build();
    }

        @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 3)
        )
    @Auditable(module = "ARCE_CLIENTE_SERVICE", action = "GET_LICITACIONES_FROM_ARCE")
    public CompletableFuture<List<LicitacionDTO>> obtenerLicitaciones(ArceRssFilters filters) {
        try {
            String rssPath = urlStrategyResolver.buildPath(filters);
            Integer resolvedFamilyCod = filters.familyCod();
            Integer resolvedSubFamilyCod = filters.subFamilyCod();

            RssResponseDTO response = restClient.get()
                    .uri(rssPath)
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .body(RssResponseDTO.class);

            if (response == null || response.channel() == null || response.channel().items() == null) {
                return CompletableFuture.completedFuture(List.of());
            }

            if (resolvedFamilyCod == null || resolvedSubFamilyCod == null) {
                return null;
            }

            List<LicitacionDTO> resultados = response.channel().items().stream()
                    .map(item -> {

                        LicitacionItemRecord record = new LicitacionItemRecord(
                                item.titulo(),
                                item.descripcion(),
                                item.link(),
                                item.fechaPublicacion(),
                                null,
                                resolvedFamilyCod,
                                resolvedSubFamilyCod
                        );
                        return licitacionService.save(record);
                    })
                    .toList();

            return CompletableFuture.completedFuture(resultados);

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con ARCE RSS: " + e.getMessage(), e);
        }
    }

    public String obtenerUrlConsulta(ArceRssFilters filters) {
        ArceRssFilters safeFilters = filters == null ? ArceRssFilters.empty() : filters;
        return ARCE_BASE_URL + urlStrategyResolver.buildPath(safeFilters);
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

    @Recover
    public CompletableFuture<List<LicitacionDTO>> recover(Exception e, ArceRssFilters filters) {
        Integer familyCod = filters != null ? filters.familyCod() : null;
        String traceId = org.slf4j.MDC.get("traceId");
        String errorMessage = "FALTA: Se agotaron los 5 reintentos para filtros RSS";

        System.err.println(errorMessage + " | Detalle: " + e.getMessage());

        auditService.saveAuditLog(
                traceId, "ARCE_CLIENT_SERVICE", "RECOVER_MODE",
                errorMessage, e.getMessage(), "FATAL"
        );

        if (familyCod != null) {
            System.err.println("Familia asociada al recover: " + familyCod);
        }

        return CompletableFuture.completedFuture(List.of());
    }
}