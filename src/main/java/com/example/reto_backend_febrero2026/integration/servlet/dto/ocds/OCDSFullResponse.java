package com.example.reto_backend_febrero2026.integration.servlet.dto.ocds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OCDSFullResponse(
        String uri,
        String version,
        String publishedDate,
        Publisher publisher,
        List<Release> releases
) {
    public record Publisher(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Release(
            String id,
            String date,
            List<String> tag,
            List<Party> parties,
            Buyer buyer,
            Tender tender
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Party(
            String id,
            String name,
            Identifier identifier,
            ContactPoint contactPoint,
            List<String> roles
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContactPoint(String name, String email, String telephone, String faxNumber) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tender(
            String id,
            String title,
            String descripcion,
            String status,
            List<ItemOCDS> items,
            String procurementMethod,
            String procurementMethodDetails,
            List<String> submissionMethod,
            String submissionMethodDetails,
            Period tenderPeriod,
            Period enquiryPeriod,
            List<Document> documents
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ItemOCDS(
            String id,
            String descripcion,
            Classification classification,
            Integer quantity,
            Unit unit
    ) {}

    public record Period(String startDate, String endDate) {}
    public record Document(String id, String documentType, String url, String format) {}
    public record Identifier(String id, String legalName) {}
    public record Buyer(String id, String name) {}
    public record Classification(String id, String descripcion) {}
    public record Unit(String id, String name) {}
}