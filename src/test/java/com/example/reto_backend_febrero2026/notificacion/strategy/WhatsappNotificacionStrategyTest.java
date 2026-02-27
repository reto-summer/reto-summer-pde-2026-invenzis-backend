package com.example.reto_backend_febrero2026.notificacion.strategy;

import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import com.example.reto_backend_febrero2026.notificacion.whatsapp.IWhatsappMetaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhatsappNotificacionStrategyTest {

    @Mock
    private IWhatsappMetaService whatsappMetaService;

    @InjectMocks
    private WhatsappNotificacionStrategy strategy;

    // ===== getNotificacionType =====

    @Test
    void getNotificacionType_deberiaRetornarWHATSAPP() {
        assertEquals(NotificacionType.WHATSAPP, strategy.getNotificacionType());
    }

    // ===== send exitoso =====

    @Test
    void send_envioExitoso_deberiaRetornarNotificacionConExito() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Licitaciones", true, "Detalle original", "Contenido", fecha);

        // Assert
        assertEquals("[WHATSAPP] Licitaciones", resultado.getTitulo());
        assertTrue(resultado.getExito());
        assertTrue(resultado.getDetalle().contains("Envío WhatsApp Meta OK"));
        assertTrue(resultado.getDetalle().contains("Detalle original"));
        assertEquals("Contenido", resultado.getContenido());
        assertEquals(fecha, resultado.getFechaEjecucion());
        verify(whatsappMetaService).sendTextMessage(anyString());
    }

    @Test
    void send_envioExitoso_deberiaEnviarMensajeFormateado() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        strategy.send("Licitaciones nuevas", true, "5 licitaciones", "Lista HTML", fecha);

        // Assert
        verify(whatsappMetaService).sendTextMessage(argThat(msg ->
                msg.contains("Licitaciones nuevas") &&
                msg.contains("EXITOSA") &&
                msg.contains("5 licitaciones") &&
                msg.contains("Lista HTML")
        ));
    }

    @Test
    void send_conExitoFalse_deberiaEnviarMensajeFALLIDA() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        strategy.send("Error proceso", false, "Error SMTP", "Contenido", fecha);

        // Assert
        verify(whatsappMetaService).sendTextMessage(argThat(msg ->
                msg.contains("FALLIDA")
        ));
    }

    // ===== send con excepción =====

    @Test
    void send_cuandoWhatsappFalla_deberiaRetornarNotificacionFallida() {
        // Arrange
        doThrow(new RuntimeException("Connection timeout")).when(whatsappMetaService).sendTextMessage(anyString());
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Licitaciones", true, "Detalle", "Contenido", fecha);

        // Assert
        assertEquals("[WHATSAPP] Licitaciones", resultado.getTitulo());
        assertFalse(resultado.getExito());
        assertTrue(resultado.getDetalle().contains("Error WhatsApp Meta: Connection timeout"));
    }

    @Test
    void send_cuandoWhatsappFalla_deberiaPreservarDetallePrevio() {
        // Arrange
        doThrow(new RuntimeException("API error")).when(whatsappMetaService).sendTextMessage(anyString());
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Test", true, "Detalle previo", "Contenido", fecha);

        // Assert
        assertTrue(resultado.getDetalle().contains("Detalle previo"));
        assertTrue(resultado.getDetalle().contains("Error WhatsApp Meta: API error"));
    }

    // ===== send con detalle null/blank =====

    @Test
    void send_conDetalleNull_deberiaAppendSinSeparador() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Test", true, null, "Contenido", fecha);

        // Assert
        assertEquals("Envío WhatsApp Meta OK", resultado.getDetalle());
    }

    @Test
    void send_conDetalleBlank_deberiaAppendSinSeparador() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Test", true, "  ", "Contenido", fecha);

        // Assert
        assertEquals("Envío WhatsApp Meta OK", resultado.getDetalle());
    }

    @Test
    void send_conContenidoNull_deberiaUsarGuionEnMensaje() {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        strategy.send("Test", true, "Detalle", null, fecha);

        // Assert
        verify(whatsappMetaService).sendTextMessage(argThat(msg ->
                msg.contains("Contenido: -")
        ));
    }
}
