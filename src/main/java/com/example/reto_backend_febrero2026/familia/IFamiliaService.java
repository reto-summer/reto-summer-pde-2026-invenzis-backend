package com.example.reto_backend_febrero2026.familia;

import java.util.List;

public interface IFamiliaService {
    List<FamiliaModel> findAll();
    FamiliaModel findById(Integer cod);
}
 