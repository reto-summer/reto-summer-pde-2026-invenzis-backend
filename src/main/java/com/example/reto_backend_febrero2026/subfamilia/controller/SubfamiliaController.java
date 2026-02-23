package com.example.reto_backend_febrero2026.subfamilia.controller;

import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaModelDTO;
import com.example.reto_backend_febrero2026.subfamilia.service.interfaces.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subfamilias")
@CrossOrigin(origins = "*")
public class SubfamiliaController {

    private final ISubfamiliaService subfamiliaService;

    public SubfamiliaController(ISubfamiliaService subfamiliaService) {
        this.subfamiliaService = subfamiliaService;
    }

    @GetMapping
    public List<SubfamiliaModelDTO> findAll() {
        return subfamiliaService.findAll();
    }

    @GetMapping("/familia/{famiCod}")
    public List<SubfamiliaModelDTO> findByFamiCod(@PathVariable Integer famiCod) {
        return subfamiliaService.findByFamiCod(famiCod);
    }

    @GetMapping("/{famiCod}/{cod}")
    public SubfamiliaModelDTO findById(
            @PathVariable Integer famiCod,
            @PathVariable Integer cod) {

        return subfamiliaService.findById(famiCod, cod);
    }
}