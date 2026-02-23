package com.example.reto_backend_febrero2026.familia.repository.implementation;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.repository.interfaces.IFamiliaRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FamiliaRepository implements IFamiliaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<FamiliaModel> familiaRowMapper = new RowMapper<>() {
        @Override
        public FamiliaModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            FamiliaModel f = new FamiliaModel();
            f.setCod(rs.getInt("cod"));
            f.setDescripcion(rs.getString("descripcion"));
            return f;
        }
    };

    public FamiliaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FamiliaModel> findAll() {
        String sql = "SELECT cod, descripcion FROM familias";
        return jdbcTemplate.query(sql, familiaRowMapper);
    }

    @Override
    public FamiliaModel findById(Integer cod) {
        String sql = "SELECT cod, descripcion FROM familias WHERE cod = ?";
       try {
            return jdbcTemplate.queryForObject(sql, familiaRowMapper, cod);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("No se encontró la licitación con Id: " + cod);
        } //probar
    }

    @Override
    public FamiliaModel save(FamiliaModel subfamilia) {
        String checkSql = "SELECT COUNT(1) FROM familias WHERE cod = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, subfamilia.getCod());
        if (count != null && count > 0) {
            String updateSql = "UPDATE familias SET descripcion = ? WHERE cod = ?";
            jdbcTemplate.update(updateSql, subfamilia.getDescripcion(), subfamilia.getCod());
            return subfamilia;
        } else {
            String insertSql = "INSERT INTO familias (cod, descripcion) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, subfamilia.getCod(), subfamilia.getDescripcion());
            return subfamilia;
        }
    }
}