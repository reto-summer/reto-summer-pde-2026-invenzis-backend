package com.example.reto_backend_febrero2026.licitacion.controller;

import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import com.example.reto_backend_febrero2026.licitacion.service.interfaces.ILicitacionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
    @RequestMapping("/licitaciones")
    public class LicitacionController {

        @Autowired
        private ILicitacionService tenderService;

        @GetMapping("/ById/{id}")
        public ResponseEntity<LicitacionModelDTO> getTenderById(@PathVariable int id) {
            LicitacionModelDTO tender = tenderService.getTenderById(id);
            return ResponseEntity.ok(tender);
        }
        /*
        @PostMapping("/save") // TESTING
        public ResponseEntity<LicitacionModelDTO> saveTender(@RequestBody LicitacionModelDTO tenderDTO) {
            return ResponseEntity.ok(tenderService.saveTender(tenderDTO));
        }

        @GetMapping("/title/{titulo}")
        public ResponseEntity<LicitacionModelDTO> getTenderByTitle(@PathVariable String titulo) {
            LicitacionModelDTO tender = tenderService.getTenderByTitle(titulo);
            return ResponseEntity.ok(tender);
        }

        @GetMapping("/fecha_publicacion/{YYYY-MM-DD}")
        public ResponseEntity<LicitacionModelDTO> getTenderByfecha_publicacion(@PathVariable LocalDate fecha) {
            LicitacionModelDTO tender = tenderService.getTenderByfecha_publicacion(fecha);
            return ResponseEntity.ok(tender);
        }

        @GetMapping("/fecha_cierre/{YYYY-MM-DDTHH:MM:SS}")
        public ResponseEntity<LicitacionModelDTO> getTenderByfecha_cierre(@PathVariable LocalDateTime fecha) {
            LicitacionModelDTO tender = tenderService.getTenderByfecha_cierre(fecha);
            return ResponseEntity.ok(tender);
        }

        @GetMapping("/fecha_publicacion/desde/{YYYY-MM-DD}/hasta/{YYYY-MM-SS}")
        public ResponseEntity<LicitacionModelDTO> getTenderByfecha_inicio_fin(@PathVariable LocalDate fecha_inicio, @PathVariable LocalDate fecha_fin) {
            LicitacionModelDTO tender = tenderService.getTenderByfecha_inicio_fin(fecha_inicio, fecha_fin);
            return ResponseEntity.ok(tender);
        }
        */
    }