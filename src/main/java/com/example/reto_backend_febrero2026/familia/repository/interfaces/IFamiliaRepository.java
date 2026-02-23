package com.example.reto_backend_febrero2026.familia.repository.interfaces;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFamiliaRepository {
    List<FamiliaModel> findAll();
    FamiliaModel findById(Integer cod);
    FamiliaModel save(FamiliaModel subfamilia);
}