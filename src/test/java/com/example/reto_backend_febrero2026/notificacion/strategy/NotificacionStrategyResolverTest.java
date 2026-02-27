package com.example.reto_backend_febrero2026.notificacion.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.reto_backend_febrero2026.notificacion.NotificacionType;

class NotificacionStrategyResolverTest {

    // ===== resolve =====

    @Test
    void resolve_conTipoEMAIL_deberiaRetornarEmailStrategy() {
        // Arrange
        INotificacionStrategy emailStrategy = mock(INotificacionStrategy.class);
        when(emailStrategy.getNotificacionType()).thenReturn(NotificacionType.EMAIL);

        NotificacionStrategyResolver resolver = new NotificacionStrategyResolver(
                List.of(emailStrategy));

        // Act
        INotificacionStrategy resultado = resolver.resolve(NotificacionType.EMAIL);

        // Assert
        assertSame(emailStrategy, resultado);
    }

    @Test
    void resolve_conTipoNoRegistrado_deberiaLanzarIllegalArgument() {
        // Arrange — solo registramos EMAIL
        INotificacionStrategy emailStrategy = mock(INotificacionStrategy.class);
        when(emailStrategy.getNotificacionType()).thenReturn(NotificacionType.EMAIL);

        NotificacionStrategyResolver resolver = new NotificacionStrategyResolver(
                List.of(emailStrategy));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
            () -> resolver.resolve(null)
        );
        assertTrue(ex.getMessage().contains("No existe estrategia"));
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void constructor_conListaVacia_deberiaCrearResolverSinEstrategias() {
        // Arrange & Act
        NotificacionStrategyResolver resolver = new NotificacionStrategyResolver(List.of());

        // Assert — cualquier tipo debería fallar
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(NotificacionType.EMAIL));
    }
}
