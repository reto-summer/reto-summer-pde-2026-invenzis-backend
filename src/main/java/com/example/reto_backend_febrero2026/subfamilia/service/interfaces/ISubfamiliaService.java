package com.example.reto_backend_febrero2026.subfamilia.service.interfaces;

import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaModelDTO;

import java.util.List;

public interface ISubfamiliaService {

    List<SubfamiliaModelDTO> findAll();

    SubfamiliaModelDTO findById(Integer famiCod, Integer cod);

    List<SubfamiliaModelDTO> findByFamiCod(Integer famiCod);

    SubfamiliaModelDTO saveFamily(SubfamiliaModelDTO subFamilia);
}