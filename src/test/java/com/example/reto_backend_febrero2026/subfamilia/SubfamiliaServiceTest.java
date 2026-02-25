package com.example.reto_backend_febrero2026.subfamilia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubfamiliaServiceTest {

    @Mock
    private ISubfamiliaRepository subfamiliaRepository;

    @Mock
    private SubfamiliaMapper subfamiliaMapper;

    @InjectMocks
    private SubfamiliaService subfamiliaService;

    private Subfamilia subfamiliaBase;
    private SubfamiliaDTO subfamiliaBaseDTO;

    @BeforeEach
    void setUp() {
        
        subfamiliaBase = new Subfamilia(10, 43, "INFRAESTRUCTURA TECNOLOGICA");
        subfamiliaBaseDTO = new SubfamiliaDTO(10, 43, "INFRAESTRUCTURA TECNOLOGICA");
    }

    @Test
    void findAll_listaDto() {
        List<Subfamilia> entities = new ArrayList<>();
        entities.add(subfamiliaBase);
        Subfamilia otra = new Subfamilia(3, 10, "SERVICIOS DE TECNOLOGIAS DE LA INFORMACION Y COMUNICACION");
        entities.add(otra);

        when(subfamiliaRepository.findAll()).thenReturn(entities);
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(subfamiliaBase)).thenReturn(subfamiliaBaseDTO);
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(otra)).thenReturn(new SubfamiliaDTO(3, 10, "SERVICIOS DE TECNOLOGIAS DE LA INFORMACION Y COMUNICACION"));

        List<SubfamiliaDTO> result = subfamiliaService.findAll();

        assertNotNull(result, "findAll no deberia retornar null");
        assertEquals(2, result.size(), "findAll debería retornar 2 subfamilias");
        verify(subfamiliaRepository).findAll();
        verify(subfamiliaMapper, times(2)).subFamilyToSubfamilyDTO(any(Subfamilia.class));
    }

    @Test
    void findAll_vacio() {
        when(subfamiliaRepository.findAll()).thenReturn(new ArrayList<>());

        List<SubfamiliaDTO> result = subfamiliaService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Cuando el repo no tiene datos, la lista debe estar vacía");
        verify(subfamiliaRepository).findAll();
        verify(subfamiliaMapper, never()).subFamilyToSubfamilyDTO(any(Subfamilia.class));
    }

    //id

    @Test
    void findById_DTO() {
        Integer famiCod = 10;
        Integer cod = 43;
        Subfamilia.SubfamiliaId id = new Subfamilia.SubfamiliaId(famiCod, cod);
        when(subfamiliaRepository.findById(id)).thenReturn(Optional.of(subfamiliaBase));
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(subfamiliaBase)).thenReturn(subfamiliaBaseDTO);

        SubfamiliaDTO result = subfamiliaService.findById(famiCod, cod);

        assertNotNull(result);
        assertEquals(famiCod, result.getFamiCod());
        assertEquals(cod, result.getCod());
        verify(subfamiliaRepository).findById(id);
    }

    @Test
    void findById_noExiste() {
        Integer famiCod = 999;
        Integer cod = 999;
        Subfamilia.SubfamiliaId id = new Subfamilia.SubfamiliaId(famiCod, cod);
        when(subfamiliaRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> subfamiliaService.findById(famiCod, cod));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Subfamilia no encontrada con famiCod: 999 y cod: 999", ex.getReason(),
            "El mensaje de error debería incluir famiCod y cod");
        verify(subfamiliaRepository).findById(id);
        verify(subfamiliaMapper, never()).subFamilyToSubfamilyDTO(any(Subfamilia.class));
    }

    // findByFamiCod 

    @Test
    void findByFamiCod_listaDTO() {
        when(subfamiliaRepository.findByFamiCod(10)).thenReturn(List.of(subfamiliaBase));
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(subfamiliaBase)).thenReturn(subfamiliaBaseDTO);

        List<SubfamiliaDTO> result = subfamiliaService.findByFamiCod(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(10), result.get(0).getFamiCod());
        verify(subfamiliaRepository).findByFamiCod(10);
    }

    @Test
    void findByFamiCod_vacio() {
        when(subfamiliaRepository.findByFamiCod(999)).thenReturn(new ArrayList<>());

        List<SubfamiliaDTO> result = subfamiliaService.findByFamiCod(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subfamiliaRepository).findByFamiCod(999);
    }

    // saveFamily

    @Test
    void saveFamily() {
        SubfamiliaDTO input = new SubfamiliaDTO(10, 43, "INFRAESTRUCTURA TECNOLOGICA");
        Subfamilia mapped = new Subfamilia(10, 43, "INFRAESTRUCTURA TECNOLOGICA");

        when(subfamiliaMapper.subFamilyDTOtoSubfamily(input)).thenReturn(mapped);
        when(subfamiliaRepository.save(mapped)).thenReturn(mapped);
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(mapped)).thenReturn(subfamiliaBaseDTO);

        SubfamiliaDTO result = subfamiliaService.saveFamily(input);

        assertNotNull(result);
        assertEquals(Integer.valueOf(10), result.getFamiCod());
        assertEquals(Integer.valueOf(43), result.getCod());
        verify(subfamiliaRepository).save(mapped);
    }

    @Test
    void saveFamily_famiCodNull() {
        SubfamiliaDTO input = new SubfamiliaDTO(null, 43, "Descripcion");
        when(subfamiliaMapper.subFamilyDTOtoSubfamily(input)).thenReturn(new Subfamilia(null, 43, "Descripcion"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> subfamiliaService.saveFamily(input));

        assertTrue(ex.getMessage().contains("fami_cod y cod son obligatorios"));
        verify(subfamiliaRepository, never()).save(any(Subfamilia.class));
    }

    @Test
    void saveFamily_codNull() {
        SubfamiliaDTO input = new SubfamiliaDTO(10, null, "Descripcion");
        when(subfamiliaMapper.subFamilyDTOtoSubfamily(input)).thenReturn(new Subfamilia(10, null, "Descripcion"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> subfamiliaService.saveFamily(input));

        assertTrue(ex.getMessage().contains("fami_cod y cod son obligatorios"));
        verify(subfamiliaRepository, never()).save(any(Subfamilia.class));
    }

    @Test
    void saveFamily_descripcionNull() {
        SubfamiliaDTO input = new SubfamiliaDTO(10, 43, null);
        Subfamilia mapped = new Subfamilia(10, 43, null);
        Subfamilia saved = new Subfamilia(10, 43, "");
        SubfamiliaDTO output = new SubfamiliaDTO(10, 43, "");

        when(subfamiliaMapper.subFamilyDTOtoSubfamily(input)).thenReturn(mapped);
        when(subfamiliaRepository.save(any(Subfamilia.class))).thenReturn(saved);
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(saved)).thenReturn(output);

        SubfamiliaDTO result = subfamiliaService.saveFamily(input);

        ArgumentCaptor<Subfamilia> captor = ArgumentCaptor.forClass(Subfamilia.class);
        verify(subfamiliaRepository).save(captor.capture());
        assertEquals("", captor.getValue().getDescripcion(),
                "La descripción enviada al repository debería ser cadena vacía");
        assertEquals("", result.getDescripcion(),
                "El DTO resultante debería tener descripción vacía");
    }

    // ===== getOrCreateSubFamily =====

    @Test
    void getOrCreate_existe() {
        when(subfamiliaRepository.findByFamiCodAndCod(10, 43)).thenReturn(subfamiliaBase);
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(subfamiliaBase)).thenReturn(subfamiliaBaseDTO);

        SubfamiliaDTO result = subfamiliaService.getOrCreateSubFamily(10, 43);

        assertNotNull(result);
        assertEquals(Integer.valueOf(10), result.getFamiCod());
        assertEquals(Integer.valueOf(43), result.getCod());
        verify(subfamiliaRepository).findByFamiCodAndCod(10, 43);
        verify(subfamiliaRepository, never()).save(any(Subfamilia.class));
    }

    @Test
    void getOrCreate() {
        when(subfamiliaRepository.findByFamiCodAndCod(3, 10)).thenReturn(null);
        Subfamilia creada = new Subfamilia(3, 10, "Subfamilia 10");
        when(subfamiliaRepository.save(any(Subfamilia.class))).thenReturn(creada);
        when(subfamiliaMapper.subFamilyToSubfamilyDTO(creada)).thenReturn(new SubfamiliaDTO(3, 10, "Subfamilia 10"));

        SubfamiliaDTO result = subfamiliaService.getOrCreateSubFamily(3, 10);

        assertNotNull(result);
        assertEquals(Integer.valueOf(3), result.getFamiCod());
        assertEquals(Integer.valueOf(10), result.getCod());
        verify(subfamiliaRepository).save(any(Subfamilia.class));
    }

    @Test
    void getOrCreate_famCodNull() {
        SubfamiliaDTO result = subfamiliaService.getOrCreateSubFamily(null, 10);

        assertNull(result);
        verify(subfamiliaRepository, never()).findByFamiCodAndCod(any(), any());
        verify(subfamiliaRepository, never()).save(any(Subfamilia.class));
    }

    @Test
    void getOrCreate_subCodNull() {
        SubfamiliaDTO result = subfamiliaService.getOrCreateSubFamily(10, null);

        assertNull(result);
        verify(subfamiliaRepository, never()).findByFamiCodAndCod(any(), any());
        verify(subfamiliaRepository, never()).save(any(Subfamilia.class));
    }
}