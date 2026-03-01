package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;

import java.time.LocalDate;
import java.util.List;

public interface ILicitacionService {
    List<LicitacionDTO> findByFilters(LocalDate fechaPublicacionDesde, LocalDate fechaPublicacionHasta,
                                      LocalDate fechaCierreDesde, LocalDate fechaCierreHasta, Integer familiaCod,
                                      Integer subfamiliaCod);

    LicitacionDTO getLicitacionById(int id);

    List<LicitacionDTO> getLicitacionByTitulo(String titulo);

    LicitacionDTO save(LicitacionItemRecord itemRecord);

    // USAR EL FINDBYFILTERS
    List<LicitacionDTO> getLicitacionesByFamiliaAndSubfamilia(Integer familia_cod, Integer subfamilia_cod);


}