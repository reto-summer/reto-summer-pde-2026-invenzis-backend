package com.example.reto_backend_febrero2026.integration.servlet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public record LicitacionItemRecord(
        @JacksonXmlProperty(localName = "title")
        String titulo,

        @JacksonXmlProperty(localName = "description")
        String description,

        @JacksonXmlProperty(localName = "link")
        String link,

        @JacksonXmlProperty(localName = "pubDate")
        String fechaPublicacion,

        Integer familaCod,

        Integer subFamiliaCod
) {
}
