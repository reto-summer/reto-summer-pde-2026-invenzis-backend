package com.example.reto_backend_febrero2026.familia;

  import org.junit.jupiter.api.Test;
  import org.junit.jupiter.api.extension.ExtendWith;
  import org.mockito.InjectMocks;
  import org.mockito.Mock;
  import org.mockito.junit.jupiter.MockitoExtension;

  import java.util.List;
  import java.util.Optional;

  import static org.junit.jupiter.api.Assertions.*;
  import static org.mockito.Mockito.*;

  @ExtendWith(MockitoExtension.class)
  class FamiliaServiceTest {

      @Mock
      private IFamiliaRepository familiaRepository;

      @Mock
      private FamiliaMapper familiaMapper;

      @InjectMocks
      private FamiliaService familiaService;

      // testea que el servicio llama al repository y al mapper correctamente
      // findAll llama a familiaRepository.findAll() y familiaMapper.familyToFamilyDTO()
      @Test
      void findAll_deberiaRetornarListaDeDTOs() {
          // Arrange
          Familia familia1 = new Familia(1, "SERVICIOS PERSONALES");
          Familia familia2 = new Familia(2, "MATERIALES Y SUMINISTROS");
          List<Familia> familias = List.of(familia1, familia2);

          FamiliaDTO dto1 = new FamiliaDTO(1, "SERVICIOS PERSONALES");
          FamiliaDTO dto2 = new FamiliaDTO(2, "MATERIALES Y SUMINISTROS");

          when(familiaRepository.findAll()).thenReturn(familias);
          when(familiaMapper.familyToFamilyDTO(familia1)).thenReturn(dto1);
          when(familiaMapper.familyToFamilyDTO(familia2)).thenReturn(dto2);

          // Act
          List<FamiliaDTO> resultado = familiaService.findAll();

          // Assert
          assertEquals(2, resultado.size());
          assertEquals("SERVICIOS PERSONALES", resultado.get(0).getDescripcion());
          assertEquals("MATERIALES Y SUMINISTROS", resultado.get(1).getDescripcion());
          verify(familiaRepository).findAll();
      }
       
      // testea que el servicio llama al repository y al mapper correctamente
      // findAll llama a familiaRepository.findAll() y familiaMapper.familyToFamilyDTO()
      @Test                                                                                                              void findAll_listaVacia_deberiaRetornarListaVacia() {
        // Arrange                                                                                                   
        when(familiaRepository.findAll()).thenReturn(List.of());

        // Act
        List<FamiliaDTO> resultado = familiaService.findAll();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(familiaRepository).findAll();
        verifyNoInteractions(familiaMapper);
    }
    // ===== findById =====                                                                                         
    // testea que el servicio llama al repository y al mapper correctamente
    // findById llama a familiaRepository.findById() y familiaMapper.familyToFamilyDTO()
    @Test                                                                                                        
    void findById_codigoExistente_deberiaRetornarDTO() {
          // Arrange
          Familia familia = new Familia(3, "SERVICIOS NO PERSONALES");
          FamiliaDTO dto = new FamiliaDTO(3, "SERVICIOS NO PERSONALES");

          when(familiaRepository.findById(3)).thenReturn(Optional.of(familia));
          when(familiaMapper.familyToFamilyDTO(familia)).thenReturn(dto);

          // Act
          FamiliaDTO resultado = familiaService.findById(3);

          // Assert
          assertEquals(3, resultado.getCod());
          assertEquals("SERVICIOS NO PERSONALES", resultado.getDescripcion());
          verify(familiaRepository).findById(3);
      }

      // testea que el servicio llama al repository y al mapper correctamente
      // findById llama a familiaRepository.findById() y lanza IllegalArgumentException
      @Test
      void findById_codigoInexistente_deberiaLanzarIllegalArgument() {
          // Arrange
          when(familiaRepository.findById(999)).thenReturn(Optional.empty());

          // Act & Assert
          IllegalArgumentException ex = assertThrows(
                  IllegalArgumentException.class,
                  () -> familiaService.findById(999)
          );
          assertTrue(ex.getMessage().contains("No se encontró la familia con cod=999"));
          verify(familiaRepository).findById(999);
          verifyNoInteractions(familiaMapper);
      }

         // ===== saveFamily =====   
         // testea que el servicio llama al repository y al mapper correctamente
         // saveFamily llama a familiaRepository.save() y familiaMapper.familyToFamilyDTO()
      @Test                                                                                                        
      void saveFamily_dtoValido_deberiaGuardarYRetornarDTO() {
          // Arrange
          FamiliaDTO inputDto = new FamiliaDTO(10, "BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION");   
          Familia familia = new Familia(10, "BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION");
          Familia familiaSaved = new Familia(10, "BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION");     
          FamiliaDTO outputDto = new FamiliaDTO(10, "BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION");  

          when(familiaMapper.familyDTOtoFamily(inputDto)).thenReturn(familia);
          when(familiaRepository.save(familia)).thenReturn(familiaSaved);
          when(familiaMapper.familyToFamilyDTO(familiaSaved)).thenReturn(outputDto);

          // Act
          FamiliaDTO resultado = familiaService.saveFamily(inputDto);

          // Assert
          assertEquals(10, resultado.getCod());
          assertEquals("BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION", resultado.getDescripcion());   
          verify(familiaRepository).save(familia);
      }

      // testea que el servicio llama al repository y al mapper correctamente
      // saveFamily llama a familiaRepository.save() y lanza IllegalArgumentException
      @Test
      void saveFamily_codNull_deberiaLanzarIllegalArgument() {
          // Arrange
          FamiliaDTO inputDto = new FamiliaDTO(null, "SIN CODIGO");

          // Act & Assert
          IllegalArgumentException ex = assertThrows(
                  IllegalArgumentException.class,
                  () -> familiaService.saveFamily(inputDto)
          );
          assertEquals("cod no puede ser null", ex.getMessage());
          verifyNoInteractions(familiaMapper);
          verify(familiaRepository, never()).save(any());
      }

      // testea que el servicio llama al repository y al mapper correctamente
      // saveFamily llama a familiaRepository.save() y familiaMapper.familyToFamilyDTO()
      @Test
      void saveFamily_descripcionNull_deberiaSetearDescripcionVacia() {
          // Arrange
          FamiliaDTO inputDto = new FamiliaDTO(11, null);
          Familia familia = new Familia(11, "");
          Familia familiaSaved = new Familia(11, "");
          FamiliaDTO outputDto = new FamiliaDTO(11, "");

          // el servicio setea descripcion="" en el DTO antes de mapear
          when(familiaMapper.familyDTOtoFamily(inputDto)).thenReturn(familia);
          when(familiaRepository.save(familia)).thenReturn(familiaSaved);
          when(familiaMapper.familyToFamilyDTO(familiaSaved)).thenReturn(outputDto);

          // Act
          FamiliaDTO resultado = familiaService.saveFamily(inputDto);

          // Assert
          assertEquals("", resultado.getDescripcion());
          verify(familiaRepository).save(familia);
      }

  }

 