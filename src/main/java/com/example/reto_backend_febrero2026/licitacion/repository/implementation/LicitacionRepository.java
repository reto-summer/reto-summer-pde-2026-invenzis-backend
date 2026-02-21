package com.example.reto_backend_febrero2026.licitacion.repository.implementation;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.repository.interfaces.ILicitacionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Repository
public class LicitacionRepository implements ILicitacionRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<LicitacionModel> tenderRowMapper = (rs, rowNum) -> {
        LicitacionModel tender = new LicitacionModel();
        tender.setIdLicitacion(rs.getInt("id_licitacion"));
        tender.setTitle(rs.getString("title"));
        tender.setDescription(rs.getString("description"));
        tender.setFechaPublicacion(rs.getObject("fecha_publicacion", OffsetDateTime.class));
        tender.setFechaCierre(rs.getObject("fecha_cierre", LocalDateTime.class));
        tender.setLink(rs.getString("link"));
        return tender;
    };

    public LicitacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LicitacionModel getTenderById(int tenderId) {
        String sql = "SELECT * FROM licitacion WHERE id_licitacion = ?";
        try {
            return jdbcTemplate.queryForObject(sql, tenderRowMapper, tenderId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("No se encontró la licitación con Id: " + tenderId);
        }
    }

    @Override
    public LicitacionModel save(LicitacionModel tender) {
        String sql = """
            INSERT INTO licitacion
            (title, description, fecha_publicacion, fecha_cierre, link)
            VALUES (?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_licitacion"});
            ps.setString(1, tender.getTitle());
            ps.setString(2, tender.getDescription());
            ps.setObject(3, tender.getFechaPublicacion());
            ps.setObject(4, tender.getFechaCierre());
            ps.setString(5, tender.getLink());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            tender.setIdLicitacion(key.intValue());
        }

        return tender;
    }
}