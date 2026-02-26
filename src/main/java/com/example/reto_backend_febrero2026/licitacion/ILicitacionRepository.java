package com.example.reto_backend_febrero2026.licitacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILicitacionRepository extends JpaRepository<Licitacion, Integer> {

    Optional<Licitacion> findByTitulo(String titulo);

    List<Licitacion> findByFamilia_CodAndSubfamilia_Cod(Integer familiaCod, Integer subfamiliaCod);

    List<Licitacion> findByFamilia_CodAndSubfamilia_CodAndEnviadoFalse(Integer familiaCod, Integer subfamiliaCod);
}