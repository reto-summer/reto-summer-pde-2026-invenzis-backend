package com.example.reto_backend_febrero2026.familia;

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

    private static final RowMapper<Familia> familiaRowMapper = new RowMapper<>() {
        @Override
        public Familia mapRow(ResultSet rs, int rowNum) throws SQLException {
            Familia f = new Familia();
            f.setCod(rs.getInt("cod"));
            f.setDescripcion(rs.getString("descripcion"));
            return f;
        }
    };

    public FamiliaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Familia> findAll() {
        String sql = "SELECT COD, DESCRIPCION FROM FAMILIAS";
        return jdbcTemplate.query(sql, familiaRowMapper);
    }

    @Override
    public Familia findById(Integer cod) {
        String sql = "SELECT COD, DESCRIPCION FROM FAMILIAS WHERE COD = ?";
       try {
            return jdbcTemplate.queryForObject(sql, familiaRowMapper, cod);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Familia save(Familia subfamilia) {
        String checkSql = "SELECT COUNT(1) FROM FAMILIAS WHERE COD = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, subfamilia.getCod());
        if (count != null && count > 0) {
            String updateSql = "UPDATE FAMILIAS SET DESCRIPCION = ? WHERE COD = ?";
            jdbcTemplate.update(updateSql, subfamilia.getDescripcion(), subfamilia.getCod());
            return subfamilia;
        } else {
            String insertSql = "INSERT INTO FAMILIAS (COD, DESCRIPCION) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, subfamilia.getCod(), subfamilia.getDescripcion());
            return subfamilia;
        }
    }
}