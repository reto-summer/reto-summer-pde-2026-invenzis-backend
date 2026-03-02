package com.example.reto_backend_febrero2026.familia;

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
    public List<FamiliaDTO> findAll() {
        return familiaService.findAll();
    }
}