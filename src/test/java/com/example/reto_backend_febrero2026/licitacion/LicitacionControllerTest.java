package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.config.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LicitacionController.class)
@Import(GlobalExceptionHandler.class)
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

        when(licitacionService.findByFilters(null,null,null,null, null, null))
                .thenReturn(licitaciones);

        mockMvc.perform(get("/licitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idLicitacion").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Titulo 1"))
                .andExpect(jsonPath("$[1].idLicitacion").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Titulo 2"));

        verify(licitacionService).findByFilters(null, null, null, null, null, null);
    }

    @Test
    void findAllLicitacionFiltros() throws Exception {

        LicitacionDTO dto1 = new LicitacionDTO();
        dto1.setIdLicitacion(1);
        dto1.setTitulo("Titulo 1");

        LicitacionDTO dto2 = new LicitacionDTO();
        dto2.setIdLicitacion(2);
        dto2.setTitulo("Titulo 2");

        List<LicitacionDTO> licitaciones = List.of(dto1, dto2);

        when(licitacionService.findByFilters(
                LocalDate.of(2025,1,1),
                LocalDate.of(2025,12,31),
                LocalDate.of(2025,2,1),
                LocalDate.of(2025,11,30),
                10,
                20)).thenReturn(licitaciones);

        mockMvc.perform(get("/licitaciones")
                .param("fechaPublicacionDesde","2025-01-01")
                .param("fechaPublicacionHasta","2025-12-31")
                .param("fechaCierreDesde","2025-02-01")
                .param("fechaCierreHasta","2025-11-30")
                .param("familiaCod","10")
                .param("subfamiliaCod","20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idLicitacion").value(1))
                .andExpect(jsonPath("$[1].idLicitacion").value(2));

        verify(licitacionService).findByFilters(
                LocalDate.of(2025,1,1),
                LocalDate.of(2025,12,31),
                LocalDate.of(2025,2,1),
                LocalDate.of(2025,11,30),
                10,
                20
        );
    }

    @Test
    void findAllLicitacionVacio() throws Exception {

        when(licitacionService.findByFilters(null,null,null,null, null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/licitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(licitacionService).findByFilters(null, null, null, null, null, null);
    }

    @Test
    void getLicitacionById() throws Exception {

        LicitacionDTO dto = new LicitacionDTO();
        dto.setIdLicitacion(1);
        dto.setTitulo("Titulo");

        when(licitacionService.getLicitacionById(1))
                .thenReturn(dto);

        mockMvc.perform(get("/licitaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLicitacion").value(1))
                .andExpect(jsonPath("$.titulo").value("Titulo"));
    }

    @Test
    void getLicitacionByIdVacio() throws Exception {

        when(licitacionService.getLicitacionById(1))
                .thenThrow(new EntityNotFoundException("No existe licitación con id: 1"));

        mockMvc.perform(get("/licitaciones/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe licitación con id: 1"));
    }

    @Test
    void findAllLicitacion_fechaInvalida_deberiaRetornar400() throws Exception {
        mockMvc.perform(get("/licitaciones")
                        .param("fechaPublicacionDesde", "2025-99-99"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(licitacionService);
    }

    @Test
    void getLicitacionById_idNoNumerico_deberiaRetornar400() throws Exception {
        mockMvc.perform(get("/licitaciones/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(licitacionService);
    }
}
