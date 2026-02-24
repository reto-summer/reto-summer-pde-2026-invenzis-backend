package com.example.reto_backend_febrero2026;

import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.example.reto_backend_febrero2026.licitacion.LicitacionController;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    void getLicitacionById_ok() throws Exception {

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
    void getLicitacionById_notFound() throws Exception {

        when(licitacionService.getLicitacionById(1))
                .thenReturn(null);

        mockMvc.perform(get("/licitaciones/ById/1"))
                .andExpect(status().isNotFound());
    }
}
