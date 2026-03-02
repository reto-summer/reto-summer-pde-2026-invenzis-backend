package com.example.reto_backend_febrero2026.inciso;

import java.util.List;

public interface IIncisoService {

    List<IncisoDTO> getAll();
    IncisoDTO getById(Integer id);
    List<IncisoDTO> getByNombre(String nombre);
}
