package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LicitacionServiceTest {

    @Mock
    private LicitacionUtility licitacionUtility;

    @Mock
    private ILicitacionRepository licitacionRepository;

    @Mock
    private IFamiliaService familiaService;

    @Mock
    private ISubfamiliaService subfamiliaService;

    @Mock
    private LicitacionMapper licitacionMapper;

    @InjectMocks
    private LicitacionService licitacionService;

    @Test
    void SaveLicitacionSinExistir() {

        LicitacionItemRecord itemRecord =
                new LicitacionItemRecord(
                        "Titulo",
                        "Descripcion",
                        "http://www.comprasestatales.gub.uy/consultas/detalle/id/1",
                        "Tue, 24 Feb 2026 15:00:00 -0300",
                        null,
                        10,
                        20
                );

        when(licitacionUtility.extraerIdDelLink(itemRecord.link()))
                .thenReturn(Optional.of(1));

        when(licitacionRepository.findById(1))
                .thenReturn(Optional.empty());

        LicitacionDTO dto = new LicitacionDTO();
        Licitacion licitacion = new Licitacion();

        when(licitacionMapper.itemRecordToDTO(itemRecord))
                .thenReturn(dto);

        FamiliaDTO familiaDTO = new FamiliaDTO();
        familiaDTO.setCod(10);

        SubfamiliaDTO subfamiliaDTO = new SubfamiliaDTO();
        subfamiliaDTO.setCod(20);
        subfamiliaDTO.setFamiCod(familiaDTO.getCod());

        when(familiaService.findById(10)).thenReturn(familiaDTO);
        when(subfamiliaService.findById(10, 20)).thenReturn(subfamiliaDTO);

        when(licitacionMapper.licitacionDTOtoLicitacion(dto))
                .thenReturn(licitacion);

        LicitacionDTO resultado = licitacionService.cleanSave(itemRecord);

        assertNotNull(resultado);
        assertNotNull(resultado.getFamilia());
        assertNotNull(resultado.getSubfamilia());

        assertEquals(10, resultado.getFamilia().getCod());
        assertEquals(20, resultado.getSubfamilia().getCod());
        assertEquals(10, resultado.getSubfamilia().getFamiCod());

        verify(familiaService).findById(10);
        verify(subfamiliaService).findById(10, 20);
        verify(licitacionRepository).save(licitacion);
    }

    @Test
    void SaveLicitacionExistente() {

        LicitacionItemRecord itemRecord =
                new LicitacionItemRecord(
                        "Titulo",
                        "Descripcion",
                        "http://www.comprasestatales.gub.uy/consultas/detalle/id/1",
                        "Tue, 24 Feb 2026 15:00:00 -0300",
                        null,
                        10,
                        20
                );

        when(licitacionUtility.extraerIdDelLink(itemRecord.link()))
                .thenReturn(Optional.of(1));

        Licitacion existente = new Licitacion();
        LicitacionDTO dtoExistente = new LicitacionDTO();

        when(licitacionRepository.findById(1))
                .thenReturn(Optional.of(existente));

        when(licitacionMapper.licitacionToLicitacionDTO(existente))
                .thenReturn(dtoExistente);

        LicitacionDTO resultado = licitacionService.cleanSave(itemRecord);

        assertNotNull(resultado);
        assertSame(dtoExistente, resultado);

        verify(licitacionRepository).findById(1);
        verify(licitacionMapper).licitacionToLicitacionDTO(existente);

        verify(licitacionRepository, never()).save(any());
        verify(licitacionMapper, never()).itemRecordToDTO(any());
        verify(familiaService, never()).findById(any());
        verify(subfamiliaService, never()).findById(any(), any());
    }

    @Test
    void getByIdLicitacionExistente() {

        Integer id = 1;

        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(id);

        LicitacionDTO dto = new LicitacionDTO();
        dto.setIdLicitacion(id);

        when(licitacionRepository.findById(id))
                .thenReturn(Optional.of(licitacion));

        when(licitacionMapper.licitacionToLicitacionDTO(licitacion))
                .thenReturn(dto);

        LicitacionDTO resultado = licitacionService.getLicitacionById(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getIdLicitacion());

        verify(licitacionRepository).findById(id);
        verify(licitacionMapper).licitacionToLicitacionDTO(licitacion);
    }

    @Test
    void getByIdLicitacionNoExistente() {

        Integer id = 1;

        when(licitacionRepository.findById(id))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> licitacionService.getLicitacionById(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No existe licitación con id: " + id, exception.getReason());

        verify(licitacionRepository).findById(id);
        verify(licitacionMapper, never()).licitacionToLicitacionDTO(any());
    }

    @Test
    void findAllLicitacionVacio() {

        when(licitacionRepository.findAll())
                .thenReturn(List.of());

        List<LicitacionDTO> resultado = licitacionService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(licitacionRepository).findAll();
        verify(licitacionMapper, never()).licitacionToLicitacionDTO(any());
    }

    @Test
    void findAllLicitacionConDatos() {

        Licitacion lic1 = new Licitacion();
        Licitacion lic2 = new Licitacion();

        LicitacionDTO dto1 = new LicitacionDTO();
        LicitacionDTO dto2 = new LicitacionDTO();

        when(licitacionRepository.findAll())
                .thenReturn(List.of(lic1, lic2));

        when(licitacionMapper.licitacionToLicitacionDTO(lic1))
                .thenReturn(dto1);

        when(licitacionMapper.licitacionToLicitacionDTO(lic2))
                .thenReturn(dto2);

        List<LicitacionDTO> resultado = licitacionService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(dto1, resultado.get(0));
        assertEquals(dto2, resultado.get(1));

        verify(licitacionRepository).findAll();
        verify(licitacionMapper, times(2))
                .licitacionToLicitacionDTO(any());
    }
}
