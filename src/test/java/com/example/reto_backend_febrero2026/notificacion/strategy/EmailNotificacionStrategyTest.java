package com.example.reto_backend_febrero2026.notificacion.strategy;

import com.example.reto_backend_febrero2026.email.IEmailService;
import com.example.reto_backend_febrero2026.notificacion.Notificacion;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificacionStrategyTest {

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private EmailNotificacionStrategy strategy;

    // ===== getNotificacionType =====

    @Test
    void getNotificacionType_deberiaRetornarEMAIL() {
        assertEquals(NotificacionType.EMAIL, strategy.getNotificacionType());
    }

    // ===== send =====

    @Test
    void send_conDestinatariosActivos_deberiaIncluirlosEnDetalle() {
        // Arrange
        List<String> destinatarios = List.of("user1@test.com", "user2@test.com");
        when(emailService.findAllActiveEmails()).thenReturn(destinatarios);
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Licitaciones", true, "Detalle original", "Contenido", fecha);

        // Assert
        assertEquals("[EMAIL] Licitaciones", resultado.getTitulo());
        assertTrue(resultado.getExito());
        assertTrue(resultado.getDetalle().contains("Detalle original"));
        assertTrue(resultado.getDetalle().contains("user1@test.com"));
        assertTrue(resultado.getDetalle().contains("user2@test.com"));
        assertEquals("Contenido", resultado.getContenido());
        assertEquals(fecha, resultado.getFechaEjecucion());
        verify(emailService).findAllActiveEmails();
    }

    @Test
    void send_sinDestinatariosActivos_deberiaIndicarCeroDestinatarios() {
        // Arrange
        when(emailService.findAllActiveEmails()).thenReturn(List.of());
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Licitaciones", true, "Detalle", "Contenido", fecha);

        // Assert
        assertTrue(resultado.getDetalle().contains("Destinatarios activos: 0"));
    }

    @Test
    void send_conDetalleNull_deberiaRetornarSoloDestinatarios() {
        // Arrange
        List<String> destinatarios = List.of("user@test.com");
        when(emailService.findAllActiveEmails()).thenReturn(destinatarios);
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Licitaciones", true, null, "Contenido", fecha);

        // Assert
        assertEquals("Destinatarios activos: user@test.com", resultado.getDetalle());
    }

    @Test
    void send_conDetalleBlank_deberiaRetornarSoloDestinatarios() {
        // Arrange
        List<String> destinatarios = List.of("user@test.com");
        when(emailService.findAllActiveEmails()).thenReturn(destinatarios);
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Licitaciones", true, "   ", "Contenido", fecha);

        // Assert
        assertEquals("Destinatarios activos: user@test.com", resultado.getDetalle());
    }

    @Test
    void send_conExitoFalse_deberiaPreservarEstado() {
        // Arrange
        when(emailService.findAllActiveEmails()).thenReturn(List.of());
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);

        // Act
        Notificacion resultado = strategy.send("Error envío", false, "Falló SMTP", "Contenido", fecha);

        // Assert
        assertFalse(resultado.getExito());
        assertEquals("[EMAIL] Error envío", resultado.getTitulo());
    }
}
