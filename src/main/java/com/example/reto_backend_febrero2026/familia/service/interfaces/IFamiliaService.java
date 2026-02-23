package com.example.reto_backend_febrero2026.familia.service.interfaces;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.dto.FamiliaModelDTO;

import java.util.List;

public interface IFamiliaService {
    List<FamiliaModelDTO> findAll();
    FamiliaModelDTO findById(Integer cod);
    FamiliaModelDTO saveFamily(FamiliaModelDTO familia);
}
 