package com.example.reto_backend_febrero2026.familia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamiliaRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private FamiliaRepository familiaRepository;

    // ===== findAll =====
    // testea que las queries SQL se ejecutan correctamente
    // findAll llama a jdbcTemplate.query() con el SQL correcto
    @Test
    void findAll_deberiaRetornarListaDeFamilias() {
        // Arrange
        Familia familia1 = new Familia(1, "SERVICIOS PERSONALES");
        Familia familia2 = new Familia(2, "MATERIALES Y SUMINISTROS");
        List<Familia> familias = List.of(familia1, familia2);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(familias);

        // Act
        List<Familia> resultado = familiaRepository.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("SERVICIOS PERSONALES", resultado.get(0).getDescripcion());
        assertEquals("MATERIALES Y SUMINISTROS", resultado.get(1).getDescripcion());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class));
    }

    // ===== findById =====
    // testea que las queries SQL se ejecutan correctamente
    // findById llama a jdbcTemplate.queryForObject() y retorna la entidad
    @Test
    void findById_codigoExistente_deberiaRetornarFamilia() {
        // Arrange
        Familia familia = new Familia(4, "MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS");

        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(4))).thenReturn(familia);

        // Act
        Familia resultado = familiaRepository.findById(4);

        // Assert
        assertNotNull(resultado);
        assertEquals(4, resultado.getCod());
        assertEquals("MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS", resultado.getDescripcion());
    }

    // testea que las queries SQL se ejecutan correctamente
    // findById llama a jdbcTemplate.queryForObject() y retorna null
    @Test
    void findById_codigoInexistente_deberiaRetornarNull() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(999)))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Act
        Familia resultado = familiaRepository.findById(999);

        // Assert
        assertNull(resultado);
    }

    // ===== save =====
    // testea que las queries SQL se ejecutan correctamente
    // save llama a jdbcTemplate.update() y retorna la entidad
    @Test
    void save_familiaNueva_deberiaEjecutarInsert() {
        // Arrange
        Familia familia = new Familia(12, "PRODUCTOS EXCLUIDOS DEL CATALOGO UNICO DE BIENES Y SERVICIOS");

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(12))).thenReturn(0);
        when(jdbcTemplate.update(anyString(), eq(12), eq("PRODUCTOS EXCLUIDOS DEL CATALOGO UNICO DE BIENES Y SERVICIOS"))).thenReturn(1);

        // Act
        Familia resultado = familiaRepository.save(familia);

        // Assert
        assertEquals(12, resultado.getCod());
        // Verifica que se llamó al INSERT (cod, descripcion) y no al UPDATE (descripcion, cod)
        verify(jdbcTemplate).update(anyString(), eq(12), eq("PRODUCTOS EXCLUIDOS DEL CATALOGO UNICO DE BIENES Y SERVICIOS"));
    }

    // testea que las queries SQL se ejecutan correctamente
    // save llama a jdbcTemplate.update() y retorna la entidad
    @Test
    void save_familiaExistente_deberiaEjecutarUpdate() {
        // Arrange
        Familia familia = new Familia(5, "TIERRAS, EDIFICIOS Y OTROS BIENES DE USO");

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5))).thenReturn(1);
        when(jdbcTemplate.update(anyString(), eq("TIERRAS, EDIFICIOS Y OTROS BIENES DE USO"), eq(5))).thenReturn(1);

        // Act
        Familia resultado = familiaRepository.save(familia);

        // Assert
        assertEquals(5, resultado.getCod());
        // Verifica que se llamó al UPDATE (descripcion, cod) y no al INSERT (cod, descripcion)
        verify(jdbcTemplate).update(anyString(), eq("TIERRAS, EDIFICIOS Y OTROS BIENES DE USO"), eq(5));
    }
}
