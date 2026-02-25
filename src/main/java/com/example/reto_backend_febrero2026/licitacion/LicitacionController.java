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

        @GetMapping("/ById/{id}")
        public ResponseEntity<LicitacionDTO> getLicitacionById(@PathVariable int id) {

            LicitacionDTO licitacion = licitacionService.getLicitacionById(id);

            if (licitacion == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(licitacion);
        }

        @GetMapping("/title/{titulo}")
        public ResponseEntity<LicitacionDTO> getLicitacionByTitle(@PathVariable String titulo) {

            LicitacionDTO licitacion = licitacionService.getLicitacionByTitulo(titulo);

            if (licitacion == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(licitacion);
        }

        @GetMapping("/ById/{id}/enviado")
        public ResponseEntity<LicitacionDTO> updateLicitacionFlag(@PathVariable int id, @RequestParam boolean flag) {

            LicitacionDTO updated = licitacionService.updateEnviadoFlag(id, flag);

            return ResponseEntity.ok(updated);
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
    }