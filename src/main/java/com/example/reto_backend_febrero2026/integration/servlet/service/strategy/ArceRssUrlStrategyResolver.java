package com.example.reto_backend_febrero2026.integration.servlet.service.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ArceRssUrlStrategyResolver {

    private final IArceRssUrlStrategy defaultStrategy;
    private final IArceRssUrlStrategy customStrategy;

    public ArceRssUrlStrategyResolver(
            @Qualifier("arceRssDefaultUrlStrategy") IArceRssUrlStrategy defaultStrategy,
            @Qualifier("arceRssCustomUrlStrategy") IArceRssUrlStrategy customStrategy
    ) {
        this.defaultStrategy = defaultStrategy;
        this.customStrategy = customStrategy;
    }

    public String buildPath(ArceRssFilters filters) {
        ArceRssFilters safeFilters = filters == null ? ArceRssFilters.empty() : filters;
        IArceRssUrlStrategy strategy = safeFilters.hasCustomFilters() ? customStrategy : defaultStrategy;
        return strategy.buildPath(safeFilters);
    }
}
