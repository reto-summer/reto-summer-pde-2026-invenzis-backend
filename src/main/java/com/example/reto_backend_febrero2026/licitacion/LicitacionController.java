package com.example.reto_backend_febrero2026.licitacion;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
    @RequestMapping("/licitaciones")
    public class LicitacionController {

    private final ILicitacionService licitacionService;

    public LicitacionController(ILicitacionService licitacionService) {
        this.licitacionService = licitacionService;
    }

    @Operation(summary = "Listar licitaciones", description = "Lista licitaciones con filtros opcionales: fechas de publicación/cierre, familia, subfamilia.")
    @GetMapping
    public ResponseEntity<List<LicitacionDTO>> findByFilters(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPublicacionDesde,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPublicacionHasta,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCierreDesde,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCierreHasta,
                                                             @RequestParam(required = false) Integer familiaCod,
                                                             @RequestParam(required = false) Integer subfamiliaCod) {
        return ResponseEntity.ok(
                licitacionService.findByFilters(
                        fechaPublicacionDesde,
                        fechaPublicacionHasta,
                        fechaCierreDesde,
                        fechaCierreHasta,
                        familiaCod,
                        subfamiliaCod
                )
        );
    }

    @Operation(summary = "Licitación por ID", description = "Obtiene una licitación por su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<LicitacionDTO> getLicitacionById(@PathVariable Integer id) {
        return ResponseEntity.ok(licitacionService.getLicitacionById(id));
    }
}
