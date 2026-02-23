package com.example.reto_backend_febrero2026.integration.servlet.service;

import com.example.reto_backend_febrero2026.audit.Auditable;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.dto.RssResponseDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ArceClientService {

    private final RestClient restClient;

    public ArceClientService(RestClient.Builder builder) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
        this.restClient = builder
                .baseUrl("https://www.comprasestatales.gub.uy")
                .messageConverters(converters -> converters.add(converter))
                .build();
    }

    @Async
    @Auditable(module = "ARCE_CLIENTE_SERVICE",action = "GET_LICITACIONES_FROM_ARCE")
    public List<LicitacionItemRecord> obtenerLicitaciones() {
        try {
            RssResponseDTO response = restClient.get()
                    .uri("/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/3/sub-familia/10")
                    .accept(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.valueOf("application/rss+xml"))
                    .retrieve()
                    .body(RssResponseDTO.class);

            return (response != null && response.channel() != null)
                    ? response.channel().items()
                    : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con ARCE RSS: " + e.getMessage(), e);
        }
    }
}
