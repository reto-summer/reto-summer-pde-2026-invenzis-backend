package com.example.reto_backend_febrero2026.integration.servlet.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

@RestController
public class TestController {

    private final ArceClientService arceClientService;
    private final IFamiliaService familiaService;
    private final ISubfamiliaService subfamiliaService;
    private final ILicitacionService licitacionService;

    public TestController(ArceClientService service, IFamiliaService familiaService, ISubfamiliaService subfamiliaService, ILicitacionService licitacionService) {
        this.arceClientService = service;
        this.familiaService = familiaService;
        this.subfamiliaService = subfamiliaService;
        this.licitacionService = licitacionService;
    }

    @Operation(summary = "Sincronizar RSS", description = "Obtiene licitaciones del feed ARCE y las persiste. Params opc.: familyCod, subFamilyCod")
    @GetMapping("/api/save-rss")
    public CompletableFuture<List<LicitacionDTO>> saveLicitaciones(
            @RequestParam(required = false) Integer familyCod,
            @RequestParam(required = false) Integer subFamilyCod) {

        ArceRssFilters filters = new ArceRssFilters(
            familyCod,
            subFamilyCod
        );

        return arceClientService.obtenerLicitaciones(filters);
    }

    @GetMapping("/api/rss-url")
    public String getRssUrl(
            @RequestParam(required = false) Integer familyCod,
            @RequestParam(required = false) Integer subFamilyCod) {

        ArceRssFilters filters = new ArceRssFilters(
                familyCod,
                subFamilyCod
        );

        return arceClientService.obtenerUrlConsulta(filters);
    }

    @Operation(summary = "Buscar licitaciones por título", description = "Devuelve licitaciones cuyo título contiene el texto indicado.")
    @GetMapping("licitaciones/titulo/{titulo}")
    public ResponseEntity<List<LicitacionDTO>> getLicitacionByTitle(@PathVariable String titulo) {
        return ResponseEntity.ok(licitacionService.getLicitacionByTitulo(titulo));
    }


    @Operation(summary = "Licitaciones por familia y subfamilia", description = "Filtra licitaciones por código de familia y subfamilia.")
    @GetMapping("/familias/{familiaCod}/subfamilia/{subfamiliaCod}")
    public List<LicitacionDTO> getLicitacionesByFamiliaAndSubfamilia(@PathVariable Integer familiaCod, @PathVariable Integer subfamiliaCod) {
        return licitacionService.getLicitacionesByFamiliaAndSubfamilia(familiaCod, subfamiliaCod);
    }

    @Operation(summary = "Obtener familia por código", description = "Devuelve una familia ARCE por su código.")
    @GetMapping("/familia/{cod}")
    public FamiliaDTO findById(@PathVariable Integer cod) {
        return familiaService.findById(cod);
    }

    @Operation(summary = "Listar subfamilias", description = "Devuelve todas las subfamilias del catálogo ARCE.")
    @GetMapping("/subfamilias")
    public List<SubfamiliaDTO> findAll() {
        return subfamiliaService.findAll();
    }

    @Operation(summary = "Subfamilias por familia", description = "Devuelve las subfamilias de una familia dada.")
    @GetMapping("/subfamilias/familia/{famiCod}")
    public List<SubfamiliaDTO> findByFamiCod(@PathVariable Integer famiCod) {
        return subfamiliaService.findByFamiCod(famiCod);
    }
}
