package com.example.reto_backend_febrero2026.licitacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ILicitacionRepository extends JpaRepository<Licitacion, Integer> {

    List<Licitacion> findByTituloContainingIgnoreCase(String titulo);

    @Query("""
        SELECT DISTINCT l FROM Licitacion l
        LEFT JOIN FETCH l.subfamilias s
        LEFT JOIN FETCH s.familia f
        WHERE (l.fechaPublicacion >= COALESCE(:fechaPublicacionDesde, l.fechaPublicacion))
          AND (l.fechaPublicacion <= COALESCE(:fechaPublicacionHasta, l.fechaPublicacion))
          AND (l.fechaCierre >= COALESCE(:fechaCierreDesde, l.fechaCierre))
          AND (l.fechaCierre <= COALESCE(:fechaCierreHasta, l.fechaCierre))
          AND (:familiaCod IS NULL OR f.cod = :familiaCod)
          AND (:subfamiliaCod IS NULL OR s.cod = :subfamiliaCod)
        ORDER BY l.fechaCierre ASC
    """)
    List<Licitacion> findByFilters(
            @Param("fechaPublicacionDesde") LocalDate fechaPublicacionDesde,
            @Param("fechaPublicacionHasta") LocalDate fechaPublicacionHasta,
            @Param("fechaCierreDesde") LocalDateTime fechaCierreDesde,
            @Param("fechaCierreHasta") LocalDateTime fechaCierreHasta,
            @Param("familiaCod") Integer familiaCod,
            @Param("subfamiliaCod") Integer subfamiliaCod
    );
}