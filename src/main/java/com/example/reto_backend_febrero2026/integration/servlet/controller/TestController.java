package com.example.reto_backend_febrero2026.integration.servlet.controller;

import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class TestController {

    private final ArceClientService arceClientService;

    public TestController(ArceClientService service) {
        this.arceClientService = service;
    }

    @GetMapping("/api/save-rss")
    public CompletableFuture<List<LicitacionDTO>> saveLicitaciones(
            @RequestParam Integer familyCod,
            @RequestParam Integer subFamilyCod) {

        return arceClientService.obtenerLicitaciones(familyCod, subFamilyCod);
    }
}
