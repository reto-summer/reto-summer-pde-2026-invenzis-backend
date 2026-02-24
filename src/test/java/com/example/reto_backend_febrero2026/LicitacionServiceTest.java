package com.example.reto_backend_febrero2026;

import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import com.example.reto_backend_febrero2026.licitacion.*;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        when(licitacionRepository.getLicitacionById(1))
                .thenReturn(null);

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

        when(licitacionRepository.getLicitacionById(1))
                .thenReturn(existente);

        when(licitacionMapper.licitacionToLicitacionDTO(existente))
                .thenReturn(dtoExistente);

        LicitacionDTO resultado = licitacionService.cleanSave(itemRecord);

        assertNotNull(resultado);
        assertSame(dtoExistente, resultado);

        verify(licitacionRepository).getLicitacionById(1);
        verify(licitacionMapper).licitacionToLicitacionDTO(existente);

        verify(licitacionRepository, never()).save(any());
        verify(licitacionMapper, never()).itemRecordToDTO(any());
        verify(familiaService, never()).findById(any());
        verify(subfamiliaService, never()).findById(any(), any());
    }

}
