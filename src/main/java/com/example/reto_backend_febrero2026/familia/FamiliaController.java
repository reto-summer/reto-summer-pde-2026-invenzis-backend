package com.example.reto_backend_febrero2026.familia;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Listar familias", description = "Devuelve todas las categorías de productos ARCE.")
    @GetMapping
    public List<FamiliaDTO> findAll() {
        return familiaService.findAll();
    }
}