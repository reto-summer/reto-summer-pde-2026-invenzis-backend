package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;

import java.util.List;

public interface ILicitacionEmailService {

    List<LicitacionEmail> getPendientes();
}
