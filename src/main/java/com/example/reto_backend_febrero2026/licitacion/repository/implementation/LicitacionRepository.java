package com.example.reto_backend_febrero2026.licitacion.repository.implementation;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Repository
public class LicitacionRepository implements ILicitacionRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Licitacion> licitacionRowMapper = (rs, rowNum) -> {
        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(rs.getInt("id_licitacion"));
        licitacion.setTitulo(rs.getString("titulo"));
        licitacion.setDescripcion(rs.getString("descripcion"));
        licitacion.setFechaPublicacion(rs.getObject("fecha_publicacion", OffsetDateTime.class));
        licitacion.setFechaCierre(rs.getObject("fecha_cierre", LocalDateTime.class));
        licitacion.setLink(rs.getString("link"));

        // familia y subfamilia
        Integer familiaCod = rs.getObject("familia_cod", Integer.class);
        if (familiaCod != null) {
            Familia familia = new Familia();
            familia.setCod(familiaCod);
            licitacion.setFamilia(familia);
        }

        Integer subFamiCod = rs.getObject("subfami_fami_cod", Integer.class);
        Integer subCod = rs.getObject("subfami_cod", Integer.class);

        if (subFamiCod != null && subCod != null) {
            Subfamilia sub = new Subfamilia();
            sub.setFamiCod(subFamiCod);
            sub.setCod(subCod);
            licitacion.setSubfamilia(sub);
        }
        return licitacion;
    };

    public LicitacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Licitacion getLicitacionById(int licitacionId) {
        String sql = "SELECT * FROM LICITACION WHERE ID_LICITACION = ?";
        try {
            return jdbcTemplate.queryForObject(sql, licitacionRowMapper, licitacionId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Licitacion getLicitacionByTitulo(String titulo) {
        String sql = "SELECT * FROM LICITACION WHERE TITULO = ?";
        try {
            return jdbcTemplate.queryForObject(sql, licitacionRowMapper, titulo);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Licitacion save(Licitacion licitacion) {
        String sql = """
        INSERT INTO LICITACION
          (ID_LICITACION, TITULO, DESCRIPCION, FECHA_PUBLICACION, FECHA_CIERRE, LINK,
           FAMILIA_COD, SUBFAMI_FAMI_COD, SUBFAMI_COD)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, licitacion.getIdLicitacion());
            ps.setString(2, licitacion.getTitulo());
            ps.setString(3, licitacion.getDescripcion());
            ps.setObject(4, licitacion.getFechaPublicacion());
            ps.setObject(5, licitacion.getFechaCierre());
            ps.setString(6, licitacion.getLink());

            if (licitacion.getFamilia() != null) ps.setInt(7, licitacion.getFamilia().getCod());
            else ps.setNull(7, Types.INTEGER);

            if (licitacion.getSubfamilia() != null) {
                ps.setInt(8, licitacion.getSubfamilia().getFamiCod());
                ps.setInt(9, licitacion.getSubfamilia().getCod());
            } else {
                ps.setNull(8, Types.INTEGER);
                ps.setNull(9, Types.INTEGER);
            }

            return ps;
        });

        return licitacion;
    }
}