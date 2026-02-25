package com.example.reto_backend_febrero2026.licitacion;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/licitaciones")
public class LicitacionController {

    @Autowired
    private ILicitacionService licitacionService;

    @GetMapping
    public List<LicitacionDTO> getAllLicitaciones(){
        return licitacionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicitacionDTO> getLicitacionById(@PathVariable Integer id) {
        return ResponseEntity.ok(licitacionService.getLicitacionById(id));
    }

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<LicitacionDTO> getLicitacionByTitle(@PathVariable String titulo) {
        return ResponseEntity.ok(licitacionService.getLicitacionByTitulo(titulo));
    }

    @GetMapping("/{id}/enviado")
    public ResponseEntity<LicitacionDTO> updateLicitacionFlag(@PathVariable Integer id, @RequestParam boolean flag) {
        LicitacionDTO updated = licitacionService.updateEnviadoFlag(id, flag);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/familia/{familiaCod}/subfamilia/{subfamiliaCod}")
    public List<LicitacionDTO> getLicitacionesByFamiliaAndSubfamilia(@PathVariable Integer familiaCod, @PathVariable Integer subfamiliaCod) {
        return licitacionService.getLicitacionesByFamiliaAndSubfamilia(familiaCod, subfamiliaCod);
    }
}
