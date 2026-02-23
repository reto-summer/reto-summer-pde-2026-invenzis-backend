package com.example.reto_backend_febrero2026.subfamilia.repository.implementation;

import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
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

    private static final RowMapper<SubfamiliaModel> subfamiliaRowMapper =
            new RowMapper<>() {
                @Override
                public SubfamiliaModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SubfamiliaModel s = new SubfamiliaModel();
                    s.setFamiCod(rs.getInt("fami_cod"));
                    s.setCod(rs.getInt("cod"));
                    s.setDescripcion(rs.getString("descripcion"));

                    Date fechaBaja = rs.getDate("fecha_baja");
                    if (fechaBaja != null) {
                        s.setFechaBaja(fechaBaja.toLocalDate());
                    }

                    s.setMotivoBaja(rs.getString("motivo_baja"));

                    return s;
                }
            };

    public List<SubfamiliaModel> findAll() {
        String sql = """
                SELECT fami_cod, cod, descripcion, fecha_baja, motivo_baja
                FROM subflias
                """;
        return jdbcTemplate.query(sql, subfamiliaRowMapper);
    }

    public SubfamiliaModel findById(Integer famiCod, Integer cod) {
        String sql = """
                SELECT fami_cod, cod, descripcion, fecha_baja, motivo_baja
                FROM subflias
                WHERE fami_cod = ? AND cod = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, subfamiliaRowMapper, famiCod, cod);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(
                    "Subfamilia no encontrada con famiCod: "
                            + famiCod + " y cod: " + cod
            );
        }
    }

    public List<SubfamiliaModel> findByFamiCod(Integer famiCod) {
        String sql = """
                SELECT fami_cod, cod, descripcion, fecha_baja, motivo_baja
                FROM subflias
                WHERE fami_cod = ?
                """;

        return jdbcTemplate.query(sql, subfamiliaRowMapper, famiCod);
    }

    public SubfamiliaModel save(SubfamiliaModel subFamilia) {

        String checkSql = """
                SELECT COUNT(1)
                FROM subflias
                WHERE fami_cod = ? AND cod = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                checkSql,
                Integer.class,
                subFamilia.getFamiCod(),
                subFamilia.getCod()
        );

        if (count != null && count > 0) {
            String updateSql = """
                    UPDATE subflias
                    SET descripcion = ?, fecha_baja = ?, motivo_baja = ?
                    WHERE fami_cod = ? AND cod = ?
                    """;

            jdbcTemplate.update(
                    updateSql,
                    subFamilia.getDescripcion(),
                    subFamilia.getFechaBaja(),
                    subFamilia.getMotivoBaja(),
                    subFamilia.getFamiCod(),
                    subFamilia.getCod()
            );

        } else {
            String insertSql = """
                    INSERT INTO subflias
                    (fami_cod, cod, descripcion, fecha_baja, motivo_baja)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            jdbcTemplate.update(
                    insertSql,
                    subFamilia.getFamiCod(),
                    subFamilia.getCod(),
                    subFamilia.getDescripcion(),
                    subFamilia.getFechaBaja(),
                    subFamilia.getMotivoBaja()
            );
        }
        return subFamilia;
    }
}