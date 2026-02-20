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
    public List<SubfamiliaModel> findAll() {
        return subfamiliaService.findAll();
    }

    @GetMapping("/familia/{famiCod}")
    public List<SubfamiliaModel> findByFamiCod(@PathVariable Integer famiCod) {
        return subfamiliaService.findByFamiCod(famiCod);
    }

    @GetMapping("/{famiCod}/{cod}")
    public SubfamiliaModel findById(
            @PathVariable Integer famiCod,
            @PathVariable Integer cod) {

        return subfamiliaService.findById(famiCod, cod);
    }
}