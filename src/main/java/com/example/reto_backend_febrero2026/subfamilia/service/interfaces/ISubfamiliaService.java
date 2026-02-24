package com.example.reto_backend_febrero2026.subfamilia.service.interfaces;

import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaDTO;

import java.util.List;

public interface ISubfamiliaService {

    List<SubfamiliaDTO> findAll();

    SubfamiliaDTO findById(Integer famiCod, Integer cod);

    List<SubfamiliaDTO> findByFamiCod(Integer famiCod);

    SubfamiliaDTO saveFamily(SubfamiliaDTO subFamilia);
}