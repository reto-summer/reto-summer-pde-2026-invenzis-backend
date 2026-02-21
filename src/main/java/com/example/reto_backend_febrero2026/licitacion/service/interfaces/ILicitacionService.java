package com.example.reto_backend_febrero2026.licitacion.service.interfaces;

import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ILicitacionService {
    LicitacionModelDTO getTenderById(int id);
    LicitacionModelDTO saveTender(LicitacionModelDTO dto);
    //LicitacionModelDTO getTenderByTitle(String titulo);
    //LicitacionModelDTO getTenderByfecha_publicacion(LocalDate fecha);
    //LicitacionModelDTO getTenderByfecha_cierre(LocalDateTime fecha);
    //LicitacionModelDTO getTenderByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin);
}
