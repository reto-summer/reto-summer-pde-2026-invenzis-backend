package com.example.reto_backend_febrero2026.licitacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LicitacionController.class)
class LicitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ILicitacionService licitacionService;

    @Test
    void findAllLicitacion() throws Exception {

        LicitacionDTO dto1 = new LicitacionDTO();
        dto1.setIdLicitacion(1);
        dto1.setTitulo("Titulo 1");

        LicitacionDTO dto2 = new LicitacionDTO();
        dto2.setIdLicitacion(2);
        dto2.setTitulo("Titulo 2");

        List<LicitacionDTO> licitaciones = List.of(dto1, dto2);

        when(licitacionService.findAll())
                .thenReturn(licitaciones);

        mockMvc.perform(get("/licitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty())
                .andExpect(jsonPath("$[0].idLicitacion").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Titulo 1"))
                .andExpect(jsonPath("$[1].idLicitacion").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Titulo 2"));
    }

    @Test
    void findAllLicitacionVacio() throws Exception {

        when(licitacionService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/licitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getLicitacionById() throws Exception {

        LicitacionDTO dto = new LicitacionDTO();
        dto.setIdLicitacion(1);
        dto.setTitulo("Titulo");

        when(licitacionService.getLicitacionById(1))
                .thenReturn(dto);

        mockMvc.perform(get("/licitaciones/ById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_licitacion").value(1))
                .andExpect(jsonPath("$.titulo").value("Titulo"));
    }

    @Test
    void getLicitacionByIdVacio() throws Exception {

        when(licitacionService.getLicitacionById(1))
                .thenReturn(null);

        mockMvc.perform(get("/licitaciones/ById/1"))
                .andExpect(status().isNotFound());
    }
}
