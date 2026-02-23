package com.example.reto_backend_febrero2026.integration.servlet.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;


public record ChannelRecord(
        @JacksonXmlProperty(localName = "item")
        @JacksonXmlElementWrapper(useWrapping = false)
        List<LicitacionItemRecord> items
) {
}
