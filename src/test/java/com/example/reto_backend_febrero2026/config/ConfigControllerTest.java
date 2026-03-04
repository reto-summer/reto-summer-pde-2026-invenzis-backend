package com.example.reto_backend_febrero2026.config;

import com.example.reto_backend_febrero2026.config_app.GlobalExceptionHandler;
import com.example.reto_backend_febrero2026.config_entity.ConfigController;
import com.example.reto_backend_febrero2026.config_entity.ConfigDTO;
import com.example.reto_backend_febrero2026.config_entity.ConfigUpdateDTO;
import com.example.reto_backend_febrero2026.config_entity.IConfigService;
import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigController.class)
@Import(GlobalExceptionHandler.class)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IConfigService configService;

    @Test
    void getConfig200() throws Exception {
        ConfigDTO dto = new ConfigDTO(
                1,
                new FamiliaDTO(3, "SERVICIOS"),
                new SubfamiliaDTO(3, 10, "SERVICIOS TIC")
        );
        when(configService.getConfig()).thenReturn(dto);

        mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.familia.cod").value(3))
                .andExpect(jsonPath("$.subfamilia.cod").value(10));

        verify(configService).getConfig();
    }

    @Test
    void updateConfig200() throws Exception {
        ConfigDTO dto = new ConfigDTO(
                1,
                new FamiliaDTO(3, "SERVICIOS"),
                new SubfamiliaDTO(3, 10, "SERVICIOS TIC")
        );
        when(configService.updateConfig(any(ConfigUpdateDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/config")
                        .contentType("application/json")
                        .content("{\"familiaCod\":3,\"subfamiliaCod\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.familia.cod").value(3))
                .andExpect(jsonPath("$.subfamilia.cod").value(10));
    }

    @Test
    void updateConfig_invalido400() throws Exception {
        when(configService.updateConfig(any(ConfigUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("Codigos de familia/subfamilia faltantes o incorrectos"));

        mockMvc.perform(put("/config")
                        .contentType("application/json")
                        .content("{\"familiaCod\":999,\"subfamiliaCod\":999}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Codigos de familia/subfamilia faltantes o incorrectos"));
    }
}
