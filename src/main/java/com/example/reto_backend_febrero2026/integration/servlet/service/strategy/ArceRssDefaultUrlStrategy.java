package com.example.reto_backend_febrero2026.integration.servlet.service.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ArceRssDefaultUrlStrategy implements IArceRssUrlStrategy {

    @Value("${arce.rss.default.family-cod:0}")
    private Integer defaultFamilyCod;

    @Value("${arce.rss.default.subfamily-cod:0}")
    private Integer defaultSubFamilyCod;

    @Override
    public String buildPath(ArceRssFilters filters) {
        return "/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/%d/sub-familia/%d"
                .formatted(defaultFamilyCod, defaultSubFamilyCod);
    }

    protected Integer getDefaultFamilyCod() {
        return defaultFamilyCod;
    }

    protected Integer getDefaultSubFamilyCod() {
        return defaultSubFamilyCod;
    }
}
