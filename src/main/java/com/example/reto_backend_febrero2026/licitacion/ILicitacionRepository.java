package com.example.reto_backend_febrero2026.licitacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ILicitacionRepository extends JpaRepository<Licitacion, Integer> {

    @Query("""
        SELECT l FROM Licitacion l
        LEFT JOIN FETCH l.familia
        LEFT JOIN FETCH l.subfamilia
        WHERE l.titulo = :titulo
    """)
    Optional<Licitacion> getLicitacionByTitulo(String titulo);

    List<Licitacion> findByFamilia_CodAndSubfamilia_Cod(Integer familiaCod, Integer subfamiliaCod);

    @Query("""
        SELECT DISTINCT l FROM Licitacion l
        LEFT JOIN FETCH l.familia
        LEFT JOIN FETCH l.subfamilia
        JOIN LicitacionEmail le ON le.id.idLicitacion = l.idLicitacion
        WHERE l.familia.cod = :familiaCod
        AND l.subfamilia.cod = :subfamiliaCod
        AND le.id.email IN :emails
    """)
    List<Licitacion> findNoEnviadasByFamiliaAndSubfamiliaAndEmails(
        @Param("familiaCod") Integer familiaCod,
        @Param("subfamiliaCod") Integer subfamiliaCod,
        @Param("emails") List<String> emails
    );

    @Query("""
        SELECT l FROM Licitacion l
        LEFT JOIN FETCH l.familia f
        LEFT JOIN FETCH l.subfamilia s
        WHERE (l.fechaPublicacion >= COALESCE(:fechaPublicacionDesde, l.fechaPublicacion))
        AND (l.fechaPublicacion <= COALESCE(:fechaPublicacionHasta, l.fechaPublicacion))
        AND (l.fechaCierre >= COALESCE(:fechaCierreDesde, l.fechaCierre))
        AND (l.fechaCierre <= COALESCE(:fechaCierreHasta, l.fechaCierre))
        AND (f.cod = COALESCE(:familiaCod, f.cod))
        AND (s.cod = COALESCE(:subfamiliaCod, s.cod))
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
