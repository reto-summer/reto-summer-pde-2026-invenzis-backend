package com.example.reto_backend_febrero2026.licitacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILicitacionRepository extends JpaRepository<Licitacion, Integer> {

    @Query("""
        SELECT l FROM Licitacion l
        LEFT JOIN FETCH l.familia
        LEFT JOIN FETCH l.subfamilia
    """)
    List<Licitacion> findAll();

    @Query("""
        SELECT l FROM Licitacion l
        LEFT JOIN FETCH l.familia
        LEFT JOIN FETCH l.subfamilia
        WHERE l.idLicitacion = :id
    """)
    Optional<Licitacion> getLicitacionById(Integer id);


    @Query("""
        SELECT l FROM Licitacion l
        LEFT JOIN FETCH l.familia
        LEFT JOIN FETCH l.subfamilia
        WHERE l.titulo = :titulo
    """)
    Optional<Licitacion> getLicitacionByTitulo(String titulo);
    
    List<Licitacion> findByFamilia_CodAndSubfamilia_Cod(Integer familiaCod, Integer subfamiliaCod);
}