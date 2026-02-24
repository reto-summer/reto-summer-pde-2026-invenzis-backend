package com.example.reto_backend_febrero2026.subfamilia;

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
    public List<SubfamiliaDTO> findAll() {
        return subfamiliaService.findAll();
    }

    @GetMapping("/familia/{famiCod}")
    public List<SubfamiliaDTO> findByFamiCod(@PathVariable Integer famiCod) {
        return subfamiliaService.findByFamiCod(famiCod);
    }

    @GetMapping("/familia/{famiCod}/subfamilia/{cod}")
    public SubfamiliaDTO findById(@PathVariable Integer famiCod, @PathVariable Integer cod) {
        return subfamiliaService.findById(famiCod, cod);
    }
}