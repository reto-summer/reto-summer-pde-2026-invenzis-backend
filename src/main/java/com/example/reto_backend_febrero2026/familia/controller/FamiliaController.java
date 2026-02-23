package com.example.reto_backend_febrero2026.familia.controller;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.dto.FamiliaModelDTO;
import com.example.reto_backend_febrero2026.familia.service.interfaces.IFamiliaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/familias")
@CrossOrigin(origins = "*")
public class FamiliaController {

    private final IFamiliaService familiaService;

    public FamiliaController(IFamiliaService familiaService) {
        this.familiaService = familiaService;
    }

    @GetMapping
    public List<FamiliaModelDTO> findAll() {
        return familiaService.findAll();
    }

    @GetMapping("/{cod}")
    public FamiliaModelDTO findById(@PathVariable Integer cod) {
        return familiaService.findById(cod);
    }
}