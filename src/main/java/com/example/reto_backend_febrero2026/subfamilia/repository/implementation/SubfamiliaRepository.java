package com.example.reto_backend_febrero2026.subfamilia.repository.implementation;

import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SubfamiliaRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubfamiliaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Subfamilia> subfamiliaRowMapper =
            (rs, rowNum) -> {
                Subfamilia s = new Subfamilia();
                s.setFamiCod(rs.getInt("FAMI_COD"));
                s.setCod(rs.getInt("COD"));
                s.setDescripcion(rs.getString("DESCRIPCION"));
                return s;
            };

    public List<Subfamilia> findAll() {
        String sql = """
                SELECT FAMI_COD, COD, DESCRIPCION
                FROM SUBFLIAS
                """;
        return jdbcTemplate.query(sql, subfamiliaRowMapper);
    }

    public Subfamilia findById(Integer famiCod, Integer cod) {
        String sql = """
                SELECT FAMI_COD, COD, DESCRIPCION
                FROM SUBFLIAS
                WHERE FAMI_COD = ? AND COD = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, subfamiliaRowMapper, famiCod, cod);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Subfamilia> findByFamiCod(Integer famiCod) {
        String sql = """
                SELECT FAMI_COD, COD, DESCRIPCION
                FROM SUBFLIAS
                WHERE FAMI_COD = ?
                """;

        return jdbcTemplate.query(sql, subfamiliaRowMapper, famiCod);
    }

    public Subfamilia save(Subfamilia subFamilia) {

        String updateSql = """
            UPDATE SUBFLIAS
            SET DESCRIPCION = ?
            WHERE FAMI_COD = ? AND COD = ?
            """;

        int rowsAffected = jdbcTemplate.update(
                updateSql,
                subFamilia.getDescripcion(),
                subFamilia.getFamiCod(),
                subFamilia.getCod()
        );

        if (rowsAffected == 0) {

            String insertSql = """
                INSERT INTO SUBFLIAS
                (FAMI_COD, COD, DESCRIPCION)
                VALUES (?, ?, ?)
                """;

            jdbcTemplate.update(
                    insertSql,
                    subFamilia.getFamiCod(),
                    subFamilia.getCod(),
                    subFamilia.getDescripcion()
            );
        }

        return subFamilia;
    }
}