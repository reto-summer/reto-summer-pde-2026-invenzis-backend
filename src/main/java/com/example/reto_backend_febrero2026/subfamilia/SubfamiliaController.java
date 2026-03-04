package com.example.reto_backend_febrero2026.subfamilia;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subfamilias")
public class SubfamiliaController {

    private final ISubfamiliaService subfamiliaService;

    public SubfamiliaController(ISubfamiliaService subfamiliaService) {
        this.subfamiliaService = subfamiliaService;
    }

    @Operation(summary = "Obtener subfamilia", description = "Devuelve una subfamilia por clave compuesta (famiCod + cod).")
    @GetMapping("/familia/{famiCod}/subfamilia/{cod}")
    public SubfamiliaDTO findById(@PathVariable Integer famiCod, @PathVariable Integer cod) {
        return subfamiliaService.findById(famiCod, cod);
    }
}