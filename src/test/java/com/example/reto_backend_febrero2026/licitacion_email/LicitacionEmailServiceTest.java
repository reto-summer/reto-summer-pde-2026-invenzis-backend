package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.licitacion_email.*;
import com.example.reto_backend_febrero2026.email.Email;
import com.example.reto_backend_febrero2026.email.EmailDTO;
import com.example.reto_backend_febrero2026.email.EmailMapper;
import com.example.reto_backend_febrero2026.email.IEmailService;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import com.example.reto_backend_febrero2026.licitacion.LicitacionMapper;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionService;
import com.example.reto_backend_febrero2026.notificacion.INotificacionService;
import com.example.reto_backend_febrero2026.notificacion.NotificacionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicitacionEmailServiceTest {

    @Mock
    private ILicitacionEmailRepository licitacionEmailRepository;

    @Mock
    private ILicitacionService licitacionService;

    @Mock
    private IEmailService emailService;

    @Mock
    private EmailMapper emailMapper;

    @Mock
    private LicitacionMapper licitacionMapper;

    @Mock
    private IEmailTemplateService emailTemplateService;

    @Mock
    private IEmailTransportService emailTransportService;

    @Mock
    private INotificacionService notificacionService;

    @InjectMocks
    private LicitacionEmailService licitacionEmailService;

    // ── sendNotification ──────────────────────────────────────────────────────

    @Test
    void sendNotification_conLicitacionesPendientes_enviaEmailYNotificaExito() {
        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(1);
        Email email = new Email("user@example.com");
        LicitacionEmail pendiente = new LicitacionEmail(licitacion, email);

        LicitacionDTO licitacionDTO = new LicitacionDTO();
        licitacionDTO.setIdLicitacion(1);

        when(licitacionEmailRepository.findByEnviadoFalse()).thenReturn(List.of(pendiente));
        when(licitacionMapper.licitacionToLicitacionDTO(licitacion)).thenReturn(licitacionDTO);
        when(emailTemplateService.generarLicitacionesHtml(anyList(), any(LocalDateTime.class)))
                .thenReturn("<html>1 licitacion</html>");

        licitacionEmailService.sendNotification();

        verify(emailTransportService).sendHtmlEmail(
                eq(List.of("user@example.com")),
                contains("1 Licitaciones ARCE"),
                eq("<html>1 licitacion</html>")
        );
        verify(licitacionEmailRepository).updateEnviado(eq(List.of(1)), eq(Set.of("user@example.com")));

        ArgumentCaptor<String> tituloCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> exitoCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).create(
                eq(NotificacionType.EMAIL),
                tituloCaptor.capture(),
                exitoCaptor.capture(),
                detalleCaptor.capture(),
                isNull(),
                any(LocalDateTime.class)
        );
        assertEquals("Envío de licitaciones ARCE", tituloCaptor.getValue());
        assertTrue(exitoCaptor.getValue());
        assertTrue(detalleCaptor.getValue().contains("1 licitaciones enviadas"));
    }

    @Test
    void sendNotification_sinLicitacionesPendientes_enviaEmailVacioYNotifica() {
        when(licitacionEmailRepository.findByEnviadoFalse()).thenReturn(List.of());
        when(emailService.findAllActiveEmails()).thenReturn(List.of("admin@example.com"));
        when(emailTemplateService.generarLicitacionesHtml(eq(List.of()), any(LocalDateTime.class)))
                .thenReturn("<html>sin licitaciones</html>");

        licitacionEmailService.sendNotification();

        verify(emailTransportService).sendHtmlEmail(
                eq(List.of("admin@example.com")),
                contains("Sin licitaciones ARCE"),
                eq("<html>sin licitaciones</html>")
        );
        verify(licitacionEmailRepository, never()).updateEnviado(anyList(), anySet());

        ArgumentCaptor<String> tituloCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> exitoCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).create(
                eq(NotificacionType.EMAIL),
                tituloCaptor.capture(),
                exitoCaptor.capture(),
                detalleCaptor.capture(),
                isNull(),
                any(LocalDateTime.class)
        );
        assertEquals("Notificación sin licitaciones", tituloCaptor.getValue());
        assertTrue(exitoCaptor.getValue());
        assertTrue(detalleCaptor.getValue().contains("1 destinatario(s)"));
    }

    @Test
    void sendNotification_errorEnEnvio_guardaNotificacionDeErrorYRelanzaExcepcion() {
        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(1);
        Email email = new Email("user@example.com");
        LicitacionEmail pendiente = new LicitacionEmail(licitacion, email);

        LicitacionDTO licitacionDTO = new LicitacionDTO();
        licitacionDTO.setIdLicitacion(1);

        when(licitacionEmailRepository.findByEnviadoFalse()).thenReturn(List.of(pendiente));
        when(licitacionMapper.licitacionToLicitacionDTO(licitacion)).thenReturn(licitacionDTO);
        when(emailTemplateService.generarLicitacionesHtml(anyList(), any(LocalDateTime.class)))
                .thenReturn("<html>1 licitacion</html>");
        doThrow(new RuntimeException("Error SMTP al enviar"))
                .when(emailTransportService).sendHtmlEmail(anyList(), anyString(), anyString());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> licitacionEmailService.sendNotification()
        );
        assertEquals("Error SMTP al enviar", thrown.getMessage());

        ArgumentCaptor<String> tituloCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> exitoCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).create(
                eq(NotificacionType.EMAIL),
                tituloCaptor.capture(),
                exitoCaptor.capture(),
                detalleCaptor.capture(),
                isNull(),
                any(LocalDateTime.class)
        );
        assertTrue(tituloCaptor.getValue().contains("Error en envío"));
        assertFalse(exitoCaptor.getValue());
        assertEquals("Error SMTP al enviar", detalleCaptor.getValue());

        verify(licitacionEmailRepository, never()).updateEnviado(anyList(), anySet());
    }

    // ── savePendingEmails ─────────────────────────────────────────────────────

    @Test
    void savePendingEmails_sinLicitaciones_noGuardaNada() {
        when(licitacionService.findByFilters(isNull(), isNull(), any(), isNull(), isNull(), isNull()))
                .thenReturn(List.of());

        licitacionEmailService.savePendingEmails();

        verify(licitacionEmailRepository, never()).saveAll(anyList());
    }

    @Test
    void savePendingEmails_combinacionesNuevas_guardaRegistros() {
        LicitacionDTO lic = new LicitacionDTO();
        lic.setIdLicitacion(10);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setDireccionEmail("a@b.com");

        when(licitacionService.findByFilters(isNull(), isNull(), any(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(lic));
        when(emailService.findAllActive()).thenReturn(List.of(emailDTO));
        when(licitacionEmailRepository.findByLicitacionesAndEmails(eq(List.of(10)), eq(Set.of("a@b.com"))))
                .thenReturn(Set.of());

        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(10);
        Email email = new Email("a@b.com");
        when(licitacionMapper.licitacionDTOtoLicitacion(lic)).thenReturn(licitacion);
        when(emailMapper.emailDTOtoEmail(emailDTO)).thenReturn(email);

        licitacionEmailService.savePendingEmails();

        ArgumentCaptor<List<LicitacionEmail>> captor = ArgumentCaptor.forClass(List.class);
        verify(licitacionEmailRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(10, captor.getValue().get(0).getIdLicitacion());
        assertEquals("a@b.com", captor.getValue().get(0).getDireccionEmail());
    }

    @Test
    void savePendingEmails_combinacionYaExistente_noGuardaDuplicado() {
        LicitacionDTO lic = new LicitacionDTO();
        lic.setIdLicitacion(10);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setDireccionEmail("a@b.com");

        when(licitacionService.findByFilters(isNull(), isNull(), any(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(lic));
        when(emailService.findAllActive()).thenReturn(List.of(emailDTO));
        when(licitacionEmailRepository.findByLicitacionesAndEmails(eq(List.of(10)), eq(Set.of("a@b.com"))))
                .thenReturn(Set.of("10_a@b.com"));

        licitacionEmailService.savePendingEmails();

        ArgumentCaptor<List<LicitacionEmail>> captor = ArgumentCaptor.forClass(List.class);
        verify(licitacionEmailRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }
}
