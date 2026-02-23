package com.example.reto_backend_febrero2026.integration.servlet.controller;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    private final ArceClientService arceClientService;

    public TestController(ArceClientService service) {
        this.arceClientService = service;
    }

    @GetMapping("/api/save-rss")
    public String saveLicitaciones(@RequestParam Integer familyCod, @RequestParam Integer subFamilyCod ) {
        arceClientService.obtenerLicitaciones(familyCod, subFamilyCod);
        return "Importación finalizada"; //cambiar
    }
}
