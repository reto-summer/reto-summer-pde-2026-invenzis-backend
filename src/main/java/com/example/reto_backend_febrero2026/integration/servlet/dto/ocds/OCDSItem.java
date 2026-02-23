package com.example.reto_backend_febrero2026.integration.servlet.dto.ocds;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record OCDSItem(
        @JacksonXmlProperty(localName = "title")
        String title,

        @JacksonXmlProperty(localName = "pubDate")
        String pubDate,

        @JacksonXmlProperty(localName = "link")
        String link,

        @JacksonXmlProperty(localName = "guid")
        String guid,

        @JacksonXmlProperty(localName = "category")
        String category
) {
}
