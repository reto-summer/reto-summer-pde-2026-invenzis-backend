package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;

public interface ILicitacionService {
    LicitacionDTO getLicitacionById(int id);
    LicitacionDTO getLicitacionByTitulo(String titulo);
    LicitacionDTO cleanSave(LicitacionItemRecord itemRecord);
    //LicitacionModelDTO getlicitacionByfecha_publicacion(LocalDate fecha);
    //LicitacionModelDTO getlicitacionByfecha_cierre(LocalDateTime fecha);
    //LicitacionModelDTO getlicitacionByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin);
}
