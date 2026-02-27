package com.example.reto_backend_febrero2026.notificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.reto_backend_febrero2026.notificacion.strategy.INotificacionStrategy;
import com.example.reto_backend_febrero2026.notificacion.strategy.NotificacionStrategyResolver;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private INotificacionRepository notificacionRepository;

    @Mock
    private NotificacionStrategyResolver strategyResolver;

    @InjectMocks
    private NotificacionService notificacionService;

    // ===== create =====

    @Test
    void create_conTipoEMAIL_deberiaResolverEstrategiaYPersistir() {
        // Arrange
        INotificacionStrategy emailStrategy = mock(INotificacionStrategy.class);
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        Notificacion notificacion = new Notificacion("[EMAIL] Test", true, "Detalle", "Contenido", fecha);
        Notificacion guardada = new Notificacion("[EMAIL] Test", true, "Detalle", "Contenido", fecha);
        guardada.setId(1);

        when(strategyResolver.resolve(NotificacionType.EMAIL)).thenReturn(emailStrategy);
        when(emailStrategy.send("Test", true, "Detalle", "Contenido", fecha)).thenReturn(notificacion);
        when(notificacionRepository.save(notificacion)).thenReturn(guardada);

        // Act
        Notificacion resultado = notificacionService.create(
                NotificacionType.EMAIL, "Test", true, "Detalle", "Contenido", fecha);

        // Assert
        assertEquals(1, resultado.getId());
        assertEquals("[EMAIL] Test", resultado.getTitulo());
        verify(strategyResolver).resolve(NotificacionType.EMAIL);
        verify(emailStrategy).send("Test", true, "Detalle", "Contenido", fecha);
        verify(notificacionRepository).save(notificacion);
    }

    // ===== findAllResumen =====

    @Test
    void findAllResumen_deberiaRetornarListaDeDTOs() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        Notificacion n1 = new Notificacion("[EMAIL] Test1", true, "D1", "C1", fecha);
        n1.setId(1);
        Notificacion n2 = new Notificacion("[EMAIL] Test2", false, "D2", "C2", fecha);
        n2.setId(2);

        when(notificacionRepository.findAll()).thenReturn(List.of(n1, n2));

        // Act
        List<NotificacionResumenDTO> resultado = notificacionService.findAllResumen();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("[EMAIL] Test1", resultado.get(0).getTitulo());
        assertTrue(resultado.get(0).getExito());
        assertEquals("[EMAIL] Test2", resultado.get(1).getTitulo());
        assertFalse(resultado.get(1).getExito());
    }

    @Test
    void findAllResumen_sinNotificaciones_deberiaRetornarListaVacia() {
        // Arrange
        when(notificacionRepository.findAll()).thenReturn(List.of());

        // Act
        List<NotificacionResumenDTO> resultado = notificacionService.findAllResumen();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ===== findById =====

    @Test
    void findById_existente_deberiaRetornarDetalleDTO() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        Notificacion notificacion = new Notificacion("[EMAIL] Test", true, "Detalle", "Contenido", fecha);
        notificacion.setId(5);

        when(notificacionRepository.findById(5)).thenReturn(Optional.of(notificacion));

        // Act
        Optional<NotificacionDetalleDTO> resultado = notificacionService.findById(5);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(5, resultado.get().getId());
        assertEquals("[EMAIL] Test", resultado.get().getTitulo());
        assertEquals("Detalle", resultado.get().getDetalle());
        assertEquals("Contenido", resultado.get().getContenido());
    }

    @Test
    void findById_inexistente_deberiaRetornarEmpty() {
        // Arrange
        when(notificacionRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<NotificacionDetalleDTO> resultado = notificacionService.findById(999);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ===== findExitosas / findFallidas =====

    @Test
    void findExitosas_deberiaFiltrarSoloExitosas() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        Notificacion n1 = new Notificacion("[EMAIL] OK", true, "D", "C", fecha);
        n1.setId(1);

        when(notificacionRepository.findByExitoTrue()).thenReturn(List.of(n1));

        // Act
        List<NotificacionResumenDTO> resultado = notificacionService.findExitosas();

        // Assert
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getExito());
        verify(notificacionRepository).findByExitoTrue();
    }

    @Test
    void findFallidas_deberiaFiltrarSoloFallidas() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        Notificacion n1 = new Notificacion("[EMAIL] Error", false, "D", "C", fecha);
        n1.setId(1);

        when(notificacionRepository.findByExitoFalse()).thenReturn(List.of(n1));

        // Act
        List<NotificacionResumenDTO> resultado = notificacionService.findFallidas();

        // Assert
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getExito());
        verify(notificacionRepository).findByExitoFalse();
    }
}
