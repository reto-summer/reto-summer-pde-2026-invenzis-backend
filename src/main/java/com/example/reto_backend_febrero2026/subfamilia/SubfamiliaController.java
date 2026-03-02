package com.example.reto_backend_febrero2026.subfamilia;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subfamilias")
public class SubfamiliaController {

    private final ISubfamiliaService subfamiliaService;

    public SubfamiliaController(ISubfamiliaService subfamiliaService) {
        this.subfamiliaService = subfamiliaService;
    }

    @GetMapping("/familia/{famiCod}/subfamilia/{cod}")
    public SubfamiliaDTO findById(@PathVariable Integer famiCod, @PathVariable Integer cod) {
        return subfamiliaService.findById(famiCod, cod);
    }
}