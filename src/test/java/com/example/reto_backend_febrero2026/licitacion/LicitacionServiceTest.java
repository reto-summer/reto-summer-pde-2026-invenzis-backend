package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        licitacion.setSubfamilias(new java.util.ArrayList<>());

        when(licitacionMapper.itemRecordToDTO(itemRecord))
                .thenReturn(dto);

        SubfamiliaDTO subfamiliaDTO = new SubfamiliaDTO();
        subfamiliaDTO.setCod(20);
        subfamiliaDTO.setFamiCod(10);

        when(subfamiliaService.findById(10,20))
                .thenReturn(subfamiliaDTO);

        Subfamilia subfamilia = new Subfamilia();

        when(subfamiliaService.getEntityById(10,20))
                .thenReturn(subfamilia);

        when(licitacionMapper.licitacionDTOtoLicitacion(dto))
                .thenReturn(licitacion);

        when(licitacionRepository.save(any()))
                .thenReturn(licitacion);

        LicitacionDTO dtoFinal = new LicitacionDTO();
        dtoFinal.setSubfamilias(List.of(subfamiliaDTO));

        when(licitacionMapper.licitacionToLicitacionDTO(licitacion))
                .thenReturn(dtoFinal);

        LicitacionDTO resultado = licitacionService.save(itemRecord);

        assertNotNull(resultado);
        assertEquals(1, resultado.getSubfamilias().size());
        assertEquals(20, resultado.getSubfamilias().get(0).getCod());

        verify(subfamiliaService).findById(10,20);
        verify(subfamiliaService).getEntityById(10,20);
        verify(licitacionRepository, atLeastOnce()).save(any());
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
        existente.setSubfamilias(new java.util.ArrayList<>());

        when(licitacionRepository.findById(1))
                .thenReturn(Optional.of(existente));

        Subfamilia subfamilia = new Subfamilia();

        when(subfamiliaService.getEntityById(10,20))
                .thenReturn(subfamilia);

        LicitacionDTO dtoExistente = new LicitacionDTO();

        when(licitacionMapper.licitacionToLicitacionDTO(any()))
                .thenReturn(dtoExistente);

        LicitacionDTO resultado = licitacionService.save(itemRecord);

        assertNotNull(resultado);

        verify(licitacionRepository).findById(1);
        verify(subfamiliaService).getEntityById(10,20);
        verify(licitacionMapper).licitacionToLicitacionDTO(any());
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
    }

    @Test
    void findAllLicitacionVacio() {

        when(licitacionRepository.findByFilters(null,null,null,null,null,null))
                .thenReturn(List.of());

        List<LicitacionDTO> resultado =
                licitacionService.findByFilters(null,null,null,null,null,null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findAllLicitacionConTodosLosFiltros() {

        LocalDate fechaPublicacionDesde = LocalDate.of(2025,1,1);
        LocalDate fechaPublicacionHasta = LocalDate.of(2025,12,31);
        LocalDate fechaCierreDesde = LocalDate.of(2025,11,30);
        LocalDate fechaCierreHasta = LocalDate.of(2025,11,30);
        Integer familiaCod = 10;
        Integer subfamiliaCod = 20;

        LocalDateTime desde = fechaCierreDesde.atStartOfDay();
        LocalDateTime hasta = fechaCierreHasta.atTime(23,59,59,999999999);

        Licitacion lic1 = new Licitacion();
        Licitacion lic2 = new Licitacion();

        LicitacionDTO dto1 = new LicitacionDTO();
        LicitacionDTO dto2 = new LicitacionDTO();

        when(licitacionUtility.toStartOfDay(fechaCierreDesde))
                .thenReturn(desde);

        when(licitacionUtility.toEndOfDay(fechaCierreHasta))
                .thenReturn(hasta);

        when(licitacionRepository.findByFilters(
                fechaPublicacionDesde,
                fechaPublicacionHasta,
                desde,
                hasta,
                familiaCod,
                subfamiliaCod
        )).thenReturn(List.of(lic1, lic2));

        when(licitacionMapper.licitacionToLicitacionDTO(lic1))
                .thenReturn(dto1);

        when(licitacionMapper.licitacionToLicitacionDTO(lic2))
                .thenReturn(dto2);

        List<LicitacionDTO> resultado =
                licitacionService.findByFilters(
                        fechaPublicacionDesde,
                        fechaPublicacionHasta,
                        fechaCierreDesde,
                        fechaCierreHasta,
                        familiaCod,
                        subfamiliaCod
                );

        assertEquals(2, resultado.size());
    }
}