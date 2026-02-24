package com.example.reto_backend_febrero2026.licitacion.service.interfaces;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionDTO;

public interface ILicitacionService {
    LicitacionDTO getLicitacionById(int id);
    LicitacionDTO saveLicitacion(LicitacionItemRecord dto);
    LicitacionDTO getLicitacionByTitulo(String titulo);
    //LicitacionModelDTO getlicitacionByfecha_publicacion(LocalDate fecha);
    //LicitacionModelDTO getlicitacionByfecha_cierre(LocalDateTime fecha);
    //LicitacionModelDTO getlicitacionByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin);
}
