package com.example.reto_backend_febrero2026.familia;                                                                                                                                                                               
  import org.junit.jupiter.api.Test;                                                                                 import org.springframework.beans.factory.annotation.Autowired;                                                   
  import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;                                       
  import org.springframework.test.context.bean.override.mockito.MockitoBean;
  import org.springframework.test.web.servlet.MockMvc;

  import java.util.List;

  import static org.mockito.Mockito.*;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
  import static org.junit.jupiter.api.Assertions.assertThrows;

  @WebMvcTest(FamiliaController.class)
  class FamiliaControllerTest {

      @Autowired
      private MockMvc mockMvc;

      @MockitoBean
      private IFamiliaService familiaService;

      // ===== GET /familias =====

      @Test
      void findAll_deberiaRetornar200ConListaDeFamilias() throws Exception {
          // Arrange
          FamiliaDTO dto1 = new FamiliaDTO(1, "SERVICIOS PERSONALES");
          FamiliaDTO dto2 = new FamiliaDTO(2, "MATERIALES Y SUMINISTROS");

          when(familiaService.findAll()).thenReturn(List.of(dto1, dto2));

          // Act & Assert
          mockMvc.perform(get("/familias"))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.length()").value(2))
                  .andExpect(jsonPath("$[0].cod").value(1))
                  .andExpect(jsonPath("$[0].descripcion").value("SERVICIOS PERSONALES"))
                  .andExpect(jsonPath("$[1].cod").value(2))
                  .andExpect(jsonPath("$[1].descripcion").value("MATERIALES Y SUMINISTROS"));
      }

      @Test
      void findAll_sinDatos_deberiaRetornar200ConListaVacia() throws Exception {
          // Arrange
          when(familiaService.findAll()).thenReturn(List.of());

          // Act & Assert
          mockMvc.perform(get("/familias"))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.length()").value(0));
      }

      // ===== GET /familias/{cod} =====

      @Test
      void findById_codigoExistente_deberiaRetornar200ConFamilia() throws Exception {
          // Arrange
          FamiliaDTO dto = new FamiliaDTO(4, "MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS");

          when(familiaService.findById(4)).thenReturn(dto);

          // Act & Assert
          mockMvc.perform(get("/familias/4"))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.cod").value(4))
                  .andExpect(jsonPath("$.descripcion").value("MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS"));
      }

       @Test       
      void findById_codigoInexistente_deberiaLanzarExcepcion() {
          // Arrange
          when(familiaService.findById(999))
                  .thenThrow(new IllegalArgumentException("No se encontró la familia con COD=999"));

          // Act & Assert
          assertThrows(Exception.class,
                  () -> mockMvc.perform(get("/familias/999")));
      }
  }