package com.example.reto_backend_febrero2026.integration.servlet.service.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ArceRssStrategyTest {

    @Test
    void defaultStrategyShouldUseConfiguredDefaultFamilyAndSubfamily() {
        ArceRssDefaultUrlStrategy strategy = new ArceRssDefaultUrlStrategy();
        ReflectionTestUtils.setField(strategy, "defaultFamilyCod", 3);
        ReflectionTestUtils.setField(strategy, "defaultSubFamilyCod", 10);

        String path = strategy.buildPath(ArceRssFilters.empty());

        assertEquals(
                "/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/3/sub-familia/10",
                path
        );
    }

    @Test
    void customStrategyShouldOverrideOnlyProvidedFamilyAndSubfamily() {
        ArceRssCustomUrlStrategy strategy = new ArceRssCustomUrlStrategy();
        ReflectionTestUtils.setField(strategy, "defaultFamilyCod", 3);
        ReflectionTestUtils.setField(strategy, "defaultSubFamilyCod", 10);

        String path = strategy.buildPath(new ArceRssFilters(8, null));

        assertEquals(
                "/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/8/sub-familia/10",
                path
        );
    }

    @Test
    void resolverShouldUseDefaultStrategyWhenNoCustomFilters() {
        IArceRssUrlStrategy defaultStrategy = filters -> "default-path";
        IArceRssUrlStrategy customStrategy = filters -> "custom-path";
        ArceRssUrlStrategyResolver resolver = new ArceRssUrlStrategyResolver(defaultStrategy, customStrategy);

        String path = resolver.buildPath(ArceRssFilters.empty());

        assertEquals("default-path", path);
    }

    @Test
    void resolverShouldUseCustomStrategyWhenAtLeastOneFilterIsPresent() {
        IArceRssUrlStrategy defaultStrategy = filters -> "default-path";
        IArceRssUrlStrategy customStrategy = filters -> "custom-path";
        ArceRssUrlStrategyResolver resolver = new ArceRssUrlStrategyResolver(defaultStrategy, customStrategy);

        String path = resolver.buildPath(new ArceRssFilters(3, 10));

        assertEquals("custom-path", path);
    }
}
