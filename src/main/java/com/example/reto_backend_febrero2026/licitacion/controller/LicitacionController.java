package com.example.reto_backend_febrero2026.licitacion.controller;

import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.service.interfaces.ILicitacionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
    @RequestMapping("/licitaciones")
    public class LicitacionController {

        @Autowired
        private ILicitacionService licitacionService;

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