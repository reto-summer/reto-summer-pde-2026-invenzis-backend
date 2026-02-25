package com.example.reto_backend_febrero2026.licitacion;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

@RestController
    @RequestMapping("/licitaciones")
    public class LicitacionController {

        @Autowired
        private ILicitacionService licitacionService;

        @GetMapping
        public ResponseEntity<List<LicitacionDTO>> getAllLicitaciones(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPublicacionDesde,
                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPublicacionHasta,
                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCierreDesde,
                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCierreHasta,
                  @RequestParam(required = false) Integer familiaCod,
                  @RequestParam(required = false) Integer subfamiliaCod)
        {
            return ResponseEntity.ok(
                    licitacionService.findAll(
                            fechaPublicacionDesde,
                            fechaPublicacionHasta,
                            fechaCierreDesde,
                            fechaCierreHasta,
                            familiaCod,
                            subfamiliaCod
                    )
            );
        }

        @GetMapping("/{id}")
        public ResponseEntity<LicitacionDTO> getLicitacionById(@PathVariable Integer id) {
            return ResponseEntity.ok(licitacionService.getLicitacionById(id));
        }

        @GetMapping("/titulo/{titulo}")
        public ResponseEntity<LicitacionDTO> getLicitacionByTitle(@PathVariable String titulo) {
            return ResponseEntity.ok(licitacionService.getLicitacionByTitulo(titulo));
        }

        @GetMapping("/familia/{familiaCod}/subfamilia/{subfamiliaCod}")
        public List<LicitacionDTO> getLicitacionesByFamiliaAndSubfamilia(@PathVariable Integer familiaCod, @PathVariable Integer subfamiliaCod) {
            return licitacionService.getLicitacionesByFamiliaAndSubfamilia(familiaCod, subfamiliaCod);
        }
}
        /*
        @PostMapping("/save") // TESTING
        public ResponseEntity<LicitacionModelDTO> savelicitacion(@RequestBody LicitacionModelDTO licitacionDTO) {
            return ResponseEntity.ok(licitacionService.savelicitacion(licitacionDTO));
        }

        @GetMapping("/fecha_publicacion/{YYYY-MM-DD}")
        public ResponseEntity<LicitacionModelDTO> getlicitacionByfecha_publicacion(@PathVariable LocalDate fecha) {
            LicitacionModelDTO licitacion = licitacionService.getlicitacionByfecha_publicacion(fecha);
            return ResponseEntity.ok(licitacion);
        }

        @GetMapping("/fecha_cierre/{YYYY-MM-DDTHH:MM:SS}")
        public ResponseEntity<LicitacionModelDTO> getlicitacionByfecha_cierre(@PathVariable LocalDateTime fecha) {
            LicitacionModelDTO licitacion = licitacionService.getlicitacionByfecha_cierre(fecha);
            return ResponseEntity.ok(licitacion);
        }

        @GetMapping("/fecha_publicacion/desde/{YYYY-MM-DD}/hasta/{YYYY-MM-SS}")
        public ResponseEntity<LicitacionModelDTO> getlicitacionByfecha_inicio_fin(@PathVariable LocalDate fecha_inicio, @PathVariable LocalDate fecha_fin) {
            LicitacionModelDTO licitacion = licitacionService.getlicitacionByfecha_inicio_fin(fecha_inicio, fecha_fin);
            return ResponseEntity.ok(licitacion);
        }
        */
