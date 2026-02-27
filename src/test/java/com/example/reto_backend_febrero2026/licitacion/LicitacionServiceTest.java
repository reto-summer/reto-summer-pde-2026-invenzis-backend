package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        LicitacionDTO resultado = licitacionService.save(itemRecord);

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

        LicitacionDTO resultado = licitacionService.save(itemRecord);

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

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> licitacionService.getLicitacionById(id)
        );

        assertEquals("No existe licitación con id: " + id, exception.getMessage());

        verify(licitacionRepository).findById(id);
        verify(licitacionMapper, never()).licitacionToLicitacionDTO(any());
    }

    @Test
    void findAllLicitacionVacio() {

        when(licitacionRepository.findByFilters(null,null,null,null,null,null))
                .thenReturn(List.of());

        List<LicitacionDTO> resultado = licitacionService.findByFilters(null,null,null,null,null,null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(licitacionRepository).findByFilters(
                null, null, null, null, null, null
        );
        verify(licitacionMapper, never()).licitacionToLicitacionDTO(any());
    }

    @Test
    void findAllLicitacionConTodosLosFiltros() {

        LocalDate fechaPublicacionDesde = LocalDate.of(2025,1,1);
        LocalDate fechaPublicacionHasta = LocalDate.of(2025,12,31);
        LocalDate fechaCierreDesde = LocalDate.of(2025,11,30);
        LocalDate fechaCierreHasta = LocalDate.of(2025,11,30);
        Integer familiaCod = 10;
        Integer subfamiliaCod = 20;

        LocalDateTime fechaCierreDesdeConvertida =
                fechaCierreDesde.atStartOfDay();

        LocalDateTime fechaCierreHastaConvertida =
                fechaCierreHasta.atTime(23,59,59,999_999_999);

        Licitacion lic1 = new Licitacion();
        Licitacion lic2 = new Licitacion();

        LicitacionDTO dto1 = new LicitacionDTO();
        LicitacionDTO dto2 = new LicitacionDTO();

        when(licitacionUtility.toStartOfDay(fechaCierreDesde))
                .thenReturn(fechaCierreDesdeConvertida);

        when(licitacionUtility.toEndOfDay(fechaCierreHasta))
                .thenReturn(fechaCierreHastaConvertida);

        when(licitacionRepository.findByFilters(
                fechaPublicacionDesde,
                fechaPublicacionHasta,
                fechaCierreDesdeConvertida,
                fechaCierreHastaConvertida,
                familiaCod,
                subfamiliaCod
        )).thenReturn(List.of(lic1, lic2));

        when(licitacionMapper.licitacionToLicitacionDTO(lic1))
                .thenReturn(dto1);

        when(licitacionMapper.licitacionToLicitacionDTO(lic2))
                .thenReturn(dto2);

        List<LicitacionDTO> resultado = licitacionService.findByFilters(
                fechaPublicacionDesde,
                fechaPublicacionHasta,
                fechaCierreDesde,
                fechaCierreHasta,
                familiaCod,
                subfamiliaCod
        );

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(dto1, resultado.get(0));
        assertEquals(dto2, resultado.get(1));

        verify(licitacionUtility).toStartOfDay(fechaCierreDesde);
        verify(licitacionUtility).toEndOfDay(fechaCierreHasta);

        verify(licitacionRepository).findByFilters(
                fechaPublicacionDesde,
                fechaPublicacionHasta,
                fechaCierreDesdeConvertida,
                fechaCierreHastaConvertida,
                familiaCod,
                subfamiliaCod
        );

        verify(licitacionMapper, times(2))
                .licitacionToLicitacionDTO(any());
    }
}
