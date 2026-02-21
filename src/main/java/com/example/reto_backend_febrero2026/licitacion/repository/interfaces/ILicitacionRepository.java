package com.example.reto_backend_febrero2026.licitacion.repository.interfaces;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;

public interface ILicitacionRepository {
    LicitacionModel getTenderById(int tenderId);
    LicitacionModel save(LicitacionModel tender);
}
