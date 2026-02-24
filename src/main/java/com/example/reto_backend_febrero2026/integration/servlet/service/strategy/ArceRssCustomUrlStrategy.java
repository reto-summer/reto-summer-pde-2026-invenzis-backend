package com.example.reto_backend_febrero2026.integration.servlet.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class ArceRssCustomUrlStrategy extends ArceRssDefaultUrlStrategy {

    @Override
    public String buildPath(ArceRssFilters filters) {
        Integer familyCod = filters.familyCod() != null ? filters.familyCod() : getDefaultFamilyCod();
        Integer subFamilyCod = filters.subFamilyCod() != null ? filters.subFamilyCod() : getDefaultSubFamilyCod();

        return "/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/%d/sub-familia/%d"
                .formatted(familyCod, subFamilyCod);
    }
}
