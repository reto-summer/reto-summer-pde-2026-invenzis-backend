package com.example.reto_backend_febrero2026.licitacion.repository.interfaces;

import com.example.reto_backend_febrero2026.licitacion.Licitacion;

public interface ILicitacionRepository {
    Licitacion getLicitacionById(int licitacionId);
    Licitacion save(Licitacion licitacion);
    Licitacion getLicitacionByTitulo(String titulo);
}