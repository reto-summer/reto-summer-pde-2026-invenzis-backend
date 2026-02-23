package com.example.reto_backend_febrero2026.licitacion.service.interfaces;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ILicitacionService {
    LicitacionModelDTO getTenderById(int id);
    LicitacionModelDTO saveTender(LicitacionItemRecord dto);
    LicitacionModelDTO getTenderByTitle(String titulo);
    //LicitacionModelDTO getTenderByfecha_publicacion(LocalDate fecha);
    //LicitacionModelDTO getTenderByfecha_cierre(LocalDateTime fecha);
    //LicitacionModelDTO getTenderByfecha_inicio_fin(LocalDate fecha_inicio, LocalDate fecha_fin);
}
