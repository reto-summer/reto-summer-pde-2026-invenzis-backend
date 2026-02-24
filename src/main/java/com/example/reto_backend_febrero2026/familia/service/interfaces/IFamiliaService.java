package com.example.reto_backend_febrero2026.familia.service.interfaces;

import com.example.reto_backend_febrero2026.familia.dto.FamiliaDTO;

import java.util.List;

public interface IFamiliaService {
    List<FamiliaDTO> findAll();
    FamiliaDTO findById(Integer cod);
    FamiliaDTO saveFamily(FamiliaDTO familia);
}
 