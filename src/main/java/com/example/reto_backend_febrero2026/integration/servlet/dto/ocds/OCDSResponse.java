package com.example.reto_backend_febrero2026.integration.servlet.dto.ocds;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "rss")
public record OCDSResponse(
        @JacksonXmlProperty(localName = "channel")
        OCDSChannel channel
) {
}
