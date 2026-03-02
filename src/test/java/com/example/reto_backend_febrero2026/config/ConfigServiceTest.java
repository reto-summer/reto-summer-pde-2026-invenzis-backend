package com.example.reto_backend_febrero2026.config;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.familia.IFamiliaService;
import com.example.reto_backend_febrero2026.integration.servlet.service.ArceClientService;
import com.example.reto_backend_febrero2026.integration.servlet.service.strategy.ArceRssFilters;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.subfamilia.ISubfamiliaService;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock
    private IConfigRepository configRepository;

    @Mock
    private IFamiliaService familiaService;

    @Mock
    private ISubfamiliaService subfamiliaService;

    @Mock
    private ArceClientService arceClientService;

    @InjectMocks
    private ConfigService configService;

    @Test
    void getEntityConfig_sinRegistro_deberiaLanzarEntityNotFound() {
        when(configRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> configService.getEntityConfig());
    }

    @Test
    void getConfig_conRegistro_deberiaMapearADto() {
        Config config = new Config(1,
                new Familia(3, "SERVICIOS"),
                new Subfamilia(3, 10, "SERVICIOS TIC"));
        when(configRepository.findById(1)).thenReturn(Optional.of(config));

        ConfigDTO result = configService.getConfig();

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(3, result.getFamilia().getCod());
        assertEquals(10, result.getSubfamilia().getCod());
    }

    @Test
    void updateConfig_codigosInvalidos_deberiaLanzarIllegalArgument() {
        Config config = new Config(1,
                new Familia(3, "SERVICIOS"),
                new Subfamilia(3, 10, "SERVICIOS TIC"));
        when(configRepository.findById(1)).thenReturn(Optional.of(config));
        when(familiaService.getEntityById(999)).thenThrow(new EntityNotFoundException("Familia no encontrada"));

        ConfigUpdateDTO input = new ConfigUpdateDTO();
        input.setFamiliaCod(999);
        input.setSubfamiliaCod(999);

        assertThrows(IllegalArgumentException.class, () -> configService.updateConfig(input));
        verify(configRepository, never()).save(any(Config.class));
    }

    @Test
    void updateConfig_valido_deberiaActualizarConfigYDispararSyncArce() {
        Config config = new Config(1,
                new Familia(3, "SERVICIOS"),
                new Subfamilia(3, 10, "SERVICIOS TIC"));
        Familia nuevaFamilia = new Familia(5, "CONSULTORIA");
        Subfamilia nuevaSubfamilia = new Subfamilia(5, 15, "DESARROLLO");

        when(configRepository.findById(1)).thenReturn(Optional.of(config));
        when(familiaService.getEntityById(5)).thenReturn(nuevaFamilia);
        when(subfamiliaService.getEntityById(5, 15)).thenReturn(nuevaSubfamilia);
        when(configRepository.save(config)).thenReturn(config);
        when(arceClientService.obtenerLicitaciones(any(ArceRssFilters.class)))
                .thenReturn(CompletableFuture.completedFuture(List.<LicitacionDTO>of()));

        ConfigUpdateDTO input = new ConfigUpdateDTO();
        input.setFamiliaCod(5);
        input.setSubfamiliaCod(15);
        ConfigDTO result = configService.updateConfig(input);

        assertEquals(5, result.getFamilia().getCod());
        assertEquals(15, result.getSubfamilia().getCod());
        verify(configRepository).save(config);
        verify(arceClientService).obtenerLicitaciones(new ArceRssFilters(5, 15));
    }
}
