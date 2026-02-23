package com.example.reto_backend_febrero2026.integration.servlet.dto.ocds;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record OCDSChannel(
        @JacksonXmlProperty(localName = "title")
        String title,

        @JacksonXmlProperty(localName = "item")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<OCDSItem> items

) {
}
