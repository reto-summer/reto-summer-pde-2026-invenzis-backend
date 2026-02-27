package com.example.reto_backend_febrero2026.notificacion.strategy;

import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacionStrategyResolverTest {

    // ===== resolve =====

    @Test
    void resolve_conTipoEMAIL_deberiaRetornarEmailStrategy() {
        // Arrange
        INotificacionStrategy emailStrategy = mock(INotificacionStrategy.class);
        INotificacionStrategy whatsappStrategy = mock(INotificacionStrategy.class);
        when(emailStrategy.getNotificacionType()).thenReturn(NotificacionType.EMAIL);
        when(whatsappStrategy.getNotificacionType()).thenReturn(NotificacionType.WHATSAPP);

        NotificacionStrategyResolver resolver = new NotificacionStrategyResolver(
                List.of(emailStrategy, whatsappStrategy));

        // Act
        INotificacionStrategy resultado = resolver.resolve(NotificacionType.EMAIL);

        // Assert
        assertSame(emailStrategy, resultado);
    }

    @Test
    void resolve_conTipoWHATSAPP_deberiaRetornarWhatsappStrategy() {
        // Arrange
        INotificacionStrategy emailStrategy = mock(INotificacionStrategy.class);
        INotificacionStrategy whatsappStrategy = mock(INotificacionStrategy.class);
        when(emailStrategy.getNotificacionType()).thenReturn(NotificacionType.EMAIL);
        when(whatsappStrategy.getNotificacionType()).thenReturn(NotificacionType.WHATSAPP);

        NotificacionStrategyResolver resolver = new NotificacionStrategyResolver(
                List.of(emailStrategy, whatsappStrategy));

        // Act
        INotificacionStrategy resultado = resolver.resolve(NotificacionType.WHATSAPP);

        // Assert
        assertSame(whatsappStrategy, resultado);
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
                () -> resolver.resolve(NotificacionType.WHATSAPP)
        );
        assertTrue(ex.getMessage().contains("No existe estrategia"));
        assertTrue(ex.getMessage().contains("WHATSAPP"));
    }

    @Test
    void constructor_conListaVacia_deberiaCrearResolverSinEstrategias() {
        // Arrange & Act
        NotificacionStrategyResolver resolver = new NotificacionStrategyResolver(List.of());

        // Assert — cualquier tipo debería fallar
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(NotificacionType.EMAIL));
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(NotificacionType.WHATSAPP));
    }
}
