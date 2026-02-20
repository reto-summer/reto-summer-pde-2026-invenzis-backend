package com.example.reto_backend_febrero2026.subfamilia;

import java.util.List;

public interface ISubfamiliaService {

    List<SubfamiliaModel> findAll();

    SubfamiliaModel findById(Integer famiCod, Integer cod);

    List<SubfamiliaModel> findByFamiCod(Integer famiCod);

}