package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.email.Email;
import com.example.reto_backend_febrero2026.channel.email.IEmailRepository;
import com.example.reto_backend_febrero2026.licitacion.ILicitacionRepository;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.LicitacionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicitacionEmailServiceTest {

    @Mock
    private ILicitacionEmailRepository licitacionEmailRepository;

    @Mock
    private ILicitacionRepository licitacionRepository;

    @Mock
    private IEmailRepository emailRepository;

    @InjectMocks
    private LicitacionEmailService licitacionEmailService;

    @Test
    void registrarPendientes_creaRegistroConEnviadoFalse() {
        LicitacionDTO dto = new LicitacionDTO();
        dto.setIdLicitacion(1);

        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(1);

        Email email = new Email("a@b.com");

        when(licitacionRepository.getReferenceById(1)).thenReturn(licitacion);
        when(licitacionEmailRepository.findByIdIdLicitacionIn(List.of(1))).thenReturn(List.of());
        when(emailRepository.getReferenceById("a@b.com")).thenReturn(email);

        licitacionEmailService.registrarPendientes(List.of(dto), List.of("a@b.com"));

        ArgumentCaptor<LicitacionEmail> captor = ArgumentCaptor.forClass(LicitacionEmail.class);
        verify(licitacionEmailRepository).save(captor.capture());

        LicitacionEmail saved = captor.getValue();
        assertFalse(saved.isEnviado());
        assertNull(saved.getFechaEnvio());
        assertEquals(1, saved.getId().getIdLicitacion());
        assertEquals("a@b.com", saved.getId().getEmail());
    }

    @Test
    void registrarEnvios_actualizaRegistroExistenteAEnviadoTrue() {
        LicitacionDTO dto = new LicitacionDTO();
        dto.setIdLicitacion(2);

        Licitacion licitacion = new Licitacion();
        licitacion.setIdLicitacion(2);

        Email email = new Email("x@y.com");
        LicitacionEmailId id = new LicitacionEmailId(2, "x@y.com");

        LicitacionEmail existente = new LicitacionEmail(licitacion, email, false);
        assertFalse(existente.isEnviado());
        assertNull(existente.getFechaEnvio());

        when(licitacionRepository.getReferenceById(2)).thenReturn(licitacion);
        when(licitacionEmailRepository.findById(id)).thenReturn(Optional.of(existente));

        licitacionEmailService.registrarEnvios(List.of(dto), List.of("x@y.com"));

        ArgumentCaptor<LicitacionEmail> captor = ArgumentCaptor.forClass(LicitacionEmail.class);
        verify(licitacionEmailRepository).save(captor.capture());

        LicitacionEmail saved = captor.getValue();
        assertTrue(saved.isEnviado());
        assertNotNull(saved.getFechaEnvio());
        assertTrue(saved.getFechaEnvio().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
