package com.example.reto_backend_febrero2026.familia;

import java.util.List;

public interface IFamiliaService {
    List<FamiliaDTO> findAll();

    FamiliaDTO findById(Integer cod);

    FamiliaDTO saveFamily(FamiliaDTO familia);

    Familia getEntityById(Integer cod);
}
 