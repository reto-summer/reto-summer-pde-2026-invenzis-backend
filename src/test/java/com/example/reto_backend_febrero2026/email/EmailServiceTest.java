package com.example.reto_backend_febrero2026.email;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private IEmailRepository emailRepository;

    @Mock
    private EmailMapper emailMapper;

    @InjectMocks
    private EmailService emailService;

    @Test
    void findAllActive_deberiaRetornarListaMapeada() {
        Email email1 = new Email("admin@example.com");
        Email email2 = new Email("user@example.com");
        EmailDTO dto1 = new EmailDTO();
        dto1.setEmail("admin@example.com");
        EmailDTO dto2 = new EmailDTO();
        dto2.setEmail("user@example.com");

        when(emailRepository.findByActivoTrue()).thenReturn(List.of(email1, email2));
        when(emailMapper.emailToEmailDTO(email1)).thenReturn(dto1);
        when(emailMapper.emailToEmailDTO(email2)).thenReturn(dto2);

        List<EmailDTO> result = emailService.findAllActive();

        assertEquals(2, result.size());
        verify(emailRepository).findByActivoTrue();
        verify(emailMapper, times(2)).emailToEmailDTO(any(Email.class));
    }

    @Test
    void findById_existente_deberiaRetornarDTO() {
        Email email = new Email("test@example.com");
        EmailDTO dto = new EmailDTO();
        dto.setEmail("test@example.com");
        when(emailRepository.findById("test@example.com")).thenReturn(Optional.of(email));
        when(emailMapper.emailToEmailDTO(email)).thenReturn(dto);

        EmailDTO result = emailService.findById("test@example.com");

        assertEquals("test@example.com", result.getEmail());
        verify(emailRepository).findById("test@example.com");
    }

    @Test
    void findById_inexistente_deberiaLanzarEntityNotFound() {
        when(emailRepository.findById("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> emailService.findById("noexiste@example.com"));
        verify(emailRepository).findById("noexiste@example.com");
        verifyNoInteractions(emailMapper);
    }

    @Test
    void create_emailInvalido_deberiaLanzarIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> emailService.create("invalidemail"));
        verifyNoInteractions(emailRepository);
    }

    @Test
    void create_emailNuevo_deberiaGuardarNormalizado() {
        Email saved = new Email("newemail@example.com");
        EmailDTO dto = new EmailDTO();
        dto.setEmail("newemail@example.com");
        when(emailRepository.findById("newemail@example.com")).thenReturn(Optional.empty());
        when(emailRepository.save(any(Email.class))).thenReturn(saved);
        when(emailMapper.emailToEmailDTO(saved)).thenReturn(dto);

        EmailDTO result = emailService.create(" NEWEMAIL@EXAMPLE.COM ");

        assertEquals("newemail@example.com", result.getEmail());
        verify(emailRepository).save(argThat(e -> "newemail@example.com".equals(e.getEmailAddress())));
    }

    @Test
    void create_emailExistenteActivo_noDeberiaGuardar() {
        Email existing = new Email("existing@example.com");
        existing.setActivo(true);
        EmailDTO dto = new EmailDTO();
        dto.setEmail("existing@example.com");
        when(emailRepository.findById("existing@example.com")).thenReturn(Optional.of(existing));
        when(emailMapper.emailToEmailDTO(existing)).thenReturn(dto);

        EmailDTO result = emailService.create("existing@example.com");

        assertEquals("existing@example.com", result.getEmail());
        verify(emailRepository, never()).save(any(Email.class));
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
    }

    @Test
    void create_emailExistenteInactivo_deberiaReactivar() {
        Email inactive = new Email("reactivate@example.com");
        inactive.setActivo(false);
        Email reactivated = new Email("reactivate@example.com");
        reactivated.setActivo(true);
        EmailDTO dto = new EmailDTO();
        dto.setEmail("reactivate@example.com");

        when(emailRepository.findById("reactivate@example.com"))
                .thenReturn(Optional.of(inactive))
                .thenReturn(Optional.of(reactivated));
        when(emailMapper.emailToEmailDTO(reactivated)).thenReturn(dto);

        EmailDTO result = emailService.create("reactivate@example.com");

        assertEquals("reactivate@example.com", result.getEmail());
        verify(emailRepository).updateActivo("reactivate@example.com", true);
        verify(emailRepository, never()).save(any(Email.class));
    }

    @Test
    void update_existente_conActivo_deberiaActualizar() {
        Email email = new Email("update@example.com");
        EmailDTO dto = new EmailDTO();
        dto.setEmail("update@example.com");
        when(emailRepository.existsById("update@example.com")).thenReturn(true);
        when(emailRepository.findById("update@example.com")).thenReturn(Optional.of(email));
        when(emailMapper.emailToEmailDTO(email)).thenReturn(dto);

        EmailDTO result = emailService.update("update@example.com", true);

        assertEquals("update@example.com", result.getEmail());
        verify(emailRepository).updateActivo("update@example.com", true);
    }

    @Test
    void update_inexistente_deberiaLanzarIllegalArgument() {
        when(emailRepository.existsById("noexiste@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> emailService.update("noexiste@example.com", true));
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
    }

    @Test
    void deactivate_existente_deberiaDesactivar() {
        when(emailRepository.existsById("deactivate@example.com")).thenReturn(true);

        emailService.deactivate("deactivate@example.com");

        verify(emailRepository).updateActivo("deactivate@example.com", false);
    }

    @Test
    void deactivate_inexistente_deberiaLanzarIllegalArgument() {
        when(emailRepository.existsById("noexiste@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> emailService.deactivate("noexiste@example.com"));
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
    }

    @Test
    void findAllActiveEmails_deberiaRetornarLista() {
        when(emailRepository.findAllActiveEmails()).thenReturn(List.of("a@a.com", "b@b.com"));

        List<String> result = emailService.findAllActiveEmails();

        assertEquals(2, result.size());
        verify(emailRepository).findAllActiveEmails();
    }
}
