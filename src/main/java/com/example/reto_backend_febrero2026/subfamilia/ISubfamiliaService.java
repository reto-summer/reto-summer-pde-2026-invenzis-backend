package com.example.reto_backend_febrero2026.subfamilia;

import java.util.List;

public interface ISubfamiliaService {

    List<SubfamiliaDTO> findAll();

    SubfamiliaDTO findById(Integer famiCod, Integer cod);

    List<SubfamiliaDTO> findByFamiCod(Integer famiCod);

    SubfamiliaDTO saveFamily(SubfamiliaDTO subFamilia);

    SubfamiliaDTO getOrCreateSubFamily(Integer famCod, Integer subCod);

    Subfamilia getEntityById(Integer famiCod, Integer cod);
}