package com.example.reto_backend_febrero2026.integration.servlet.dto;

import com.example.reto_backend_febrero2026.inciso.Inciso;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record LicitacionItemRecord(
        @JacksonXmlProperty(localName = "title")
        String titulo,

        @JacksonXmlProperty(localName = "description")
        String descripcion,

        @JacksonXmlProperty(localName = "link")
        String link,

        @JacksonXmlProperty(localName = "pubDate")
        String fechaPublicacion,

        String fechaCierre,
        Integer familiaCod,
        Integer subFamiliaCod,
        Inciso inciso
) {
}
