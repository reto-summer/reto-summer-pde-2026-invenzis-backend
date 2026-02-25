package com.example.reto_backend_febrero2026.subfamilia;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubfamiliaController.class)
class SubfamiliaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISubfamiliaService subfamiliaService;

    @Test
    void getAll() throws Exception {
        when(subfamiliaService.findAll()).thenReturn(List.of(
                new SubfamiliaDTO(10, 43, "INFRAESTRUCTURA TECNOLOGICA"),
                new SubfamiliaDTO(3, 10, "SERVICIOS TIC")
        ));

        mockMvc.perform(get("/subfamilias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].famiCod").value(10))
                .andExpect(jsonPath("$[0].cod").value(43));

        verify(subfamiliaService).findAll();
    }

    @Test
    void getAll_vacio() throws Exception {
        when(subfamiliaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/subfamilias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(subfamiliaService).findAll();
    }

    @Test
    void getByFamiCod() throws Exception {
        when(subfamiliaService.findByFamiCod(10)).thenReturn(List.of(
                new SubfamiliaDTO(10, 43, "INFRAESTRUCTURA TECNOLOGICA")
        ));

        mockMvc.perform(get("/subfamilias/familia/10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].famiCod").value(10))
                .andExpect(jsonPath("$[0].cod").value(43));

        verify(subfamiliaService).findByFamiCod(10);
    }

    @Test
    void getByFamiCod_vacio() throws Exception {
        when(subfamiliaService.findByFamiCod(999)).thenReturn(List.of());

        mockMvc.perform(get("/subfamilias/familia/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(subfamiliaService).findByFamiCod(999);
    }

    @Test
    void getById() throws Exception {
        when(subfamiliaService.findById(10, 43))
                .thenReturn(new SubfamiliaDTO(10, 43, "INFRAESTRUCTURA TECNOLOGICA"));

        mockMvc.perform(get("/subfamilias/familia/10/subfamilia/43")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.famiCod").value(10))
                .andExpect(jsonPath("$.cod").value(43))
                .andExpect(jsonPath("$.descripcion").value("INFRAESTRUCTURA TECNOLOGICA"));

        verify(subfamiliaService).findById(10, 43);
    }

    @Test
    void getById_noExiste() {
        when(subfamiliaService.findById(10, 999))
                .thenThrow(new RuntimeException("Subfamilia no encontrada"));

        assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/subfamilias/familia/10/subfamilia/999")
                        .accept(MediaType.APPLICATION_JSON))
        );

        verify(subfamiliaService).findById(10, 999);
    }

    @Test
    void getById_paramInvalido() throws Exception {
        mockMvc.perform(get("/subfamilias/familia/no-numero/subfamilia/43")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(subfamiliaService);
    }
}
