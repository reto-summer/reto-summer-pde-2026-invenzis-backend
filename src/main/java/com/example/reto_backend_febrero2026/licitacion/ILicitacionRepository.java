package com.example.reto_backend_febrero2026.licitacion;

public interface ILicitacionRepository {
    Licitacion getLicitacionById(int licitacionId);
    Licitacion save(Licitacion licitacion);
    Licitacion getLicitacionByTitulo(String titulo);
}