package com.example.reto_backend_febrero2026.integration.servlet.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

@RestController
public class TestController {

    private final ArceClientService arceClientService;

    public TestController(ArceClientService service) {
        this.arceClientService = service;
    }

    @GetMapping("/api/save-rss")
    public CompletableFuture<List<LicitacionDTO>> saveLicitaciones(
            @RequestParam(required = false) Integer familyCod,
            @RequestParam(required = false) Integer subFamilyCod) {

        ArceRssFilters filters = new ArceRssFilters(
            familyCod,
            subFamilyCod
        );

        return arceClientService.obtenerLicitaciones(filters);
    }

    @GetMapping("/api/rss-url")
    public String getRssUrl(
            @RequestParam(required = false) Integer familyCod,
            @RequestParam(required = false) Integer subFamilyCod) {

        ArceRssFilters filters = new ArceRssFilters(
                familyCod,
                subFamilyCod
        );

        return arceClientService.obtenerUrlConsulta(filters);
    }
}
