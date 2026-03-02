package com.example.reto_backend_febrero2026.integration.servlet.service.strategy;

public record ArceRssFilters(
        Integer familyCod,
        Integer subFamilyCod
) {

    public static ArceRssFilters empty() {
        return new ArceRssFilters(null, null);
    }

    public boolean hasCustomFilters() {
        return familyCod != null
                || subFamilyCod != null;
    }
}
