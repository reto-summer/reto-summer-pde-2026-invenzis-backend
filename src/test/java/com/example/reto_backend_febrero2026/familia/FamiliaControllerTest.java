package com.example.reto_backend_febrero2026.familia;                                                                                                                                                                               
  import org.junit.jupiter.api.Test;                                                                                 import org.springframework.beans.factory.annotation.Autowired;                                                   
  import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;                                       
  import org.springframework.test.context.bean.override.mockito.MockitoBean;
  import org.springframework.test.web.servlet.MockMvc;

  import java.util.List;

  import static org.mockito.Mockito.*;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
  @WebMvcTest(FamiliaController.class)
  class FamiliaControllerTest {

      @Autowired
      private MockMvc mockMvc;

      @MockitoBean
      private IFamiliaService familiaService;

      // ===== GET /familias =====

      // testea que el controlador llama al servicio y retorna la lista de DTOs
      // devuelve status 200 y la lista de DTOs
      @Test
      void findAll_deberiaRetornar200ConListaDeFamilias() throws Exception {
          // Arrange
          FamiliaDTO dto1 = new FamiliaDTO(1, "SERVICIOS PERSONALES");
          FamiliaDTO dto2 = new FamiliaDTO(2, "MATERIALES Y SUMINISTROS");

          when(familiaService.findAll()).thenReturn(List.of(dto1, dto2));

          // Act & Assert
          mockMvc.perform(get("/familias"))
                  .andExpect(status().is(200))
                  .andExpect(jsonPath("$.length()").value(2))
                  .andExpect(jsonPath("$[0].cod").value(1))
                  .andExpect(jsonPath("$[0].descripcion").value("SERVICIOS PERSONALES"))
                  .andExpect(jsonPath("$[1].cod").value(2))
                  .andExpect(jsonPath("$[1].descripcion").value("MATERIALES Y SUMINISTROS"));
      }
      
      // testea que el controlador llama al servicio y retorna la lista de DTOs
      // devuelve status 200 y la lista de DTOs vacía
      @Test
      void findAll_sinDatos_deberiaRetornar200ConListaVacia() throws Exception {
          // Arrange
          when(familiaService.findAll()).thenReturn(List.of());

          // Act & Assert
          mockMvc.perform(get("/familias"))
                  .andExpect(status().is(200))
                  .andExpect(jsonPath("$.length()").value(0));
      }

      // ===== GET /familias/{cod} =====

      // testea que el controlador llama al servicio y retorna el DTO
      // devuelve status 200 y el DTO
      @Test
      void findById_codigoExistente_deberiaRetornar200ConFamilia() throws Exception {
          // Arrange
          FamiliaDTO dto = new FamiliaDTO(4, "MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS");

          when(familiaService.findById(4)).thenReturn(dto);

          // Act & Assert
          mockMvc.perform(get("/familias/4"))
                  .andExpect(status().is(200))
                  .andExpect(jsonPath("$.cod").value(4))
                  .andExpect(jsonPath("$.descripcion").value("MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS"));
      }

      // testea que el controlador llama al servicio y retorna 404
      @Test
      void findById_codigoInexistente_deberiaRetornar404() throws Exception {
          // Arrange
          when(familiaService.findById(999))
                  .thenThrow(new IllegalArgumentException("No se encontró la familia con cod=999"));

          // Act & Assert
          mockMvc.perform(get("/familias/999"))
                  .andExpect(status().is(404));
      }
  }