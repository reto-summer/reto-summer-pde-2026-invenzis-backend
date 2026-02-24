package com.example.reto_backend_febrero2026.familia;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFamiliaRepository {
    List<Familia> findAll();
    Familia findById(Integer cod);
    Familia save(Familia subfamilia);
}