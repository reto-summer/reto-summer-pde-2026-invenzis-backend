package com.example.reto_backend_febrero2026.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private IEmailRepository emailRepository;

    @Mock
    private EmailMapper emailMapper;

    @InjectMocks
    private EmailService emailService;


    @Test
    void findAllActive_deberiaRetornarListaDeDTOsMapeados() {
        Email email1 = new Email("admin@example.com");
        email1.setActivo(true);
        
        Email email2 = new Email("user@example.com");
        email2.setActivo(true);
        
        List<Email> emails = List.of(email1, email2);

        EmailDTO dto1 = new EmailDTO();
        dto1.setEmail("admin@example.com");
        dto1.setActivo(true);
        
        EmailDTO dto2 = new EmailDTO();
        dto2.setEmail("user@example.com");
        dto2.setActivo(true);

        when(emailRepository.findByActivoTrue()).thenReturn(emails);
        when(emailMapper.emailToEmailDTO(email1)).thenReturn(dto1);
        when(emailMapper.emailToEmailDTO(email2)).thenReturn(dto2);


        List<EmailDTO> resultado = emailService.findAllActive();

        assertEquals(2, resultado.size());
        assertEquals("admin@example.com", resultado.get(0).getEmail());
        assertEquals("user@example.com", resultado.get(1).getEmail());
        verify(emailRepository).findByActivoTrue();
        verify(emailMapper, times(2)).emailToEmailDTO(any(Email.class));
    }

    @Test
    void findAllActive_sinEmails_deberiaRetornarListaVacia() {
        when(emailRepository.findByActivoTrue()).thenReturn(List.of());

        List<EmailDTO> resultado = emailService.findAllActive();

        assertTrue(resultado.isEmpty());
        verify(emailRepository).findByActivoTrue();
        verifyNoInteractions(emailMapper);
    }


    @Test
    void findById_emailExistente_deberiaRetornarOptionalConDTO() {
        // Arrange
        Email email = new Email("test@example.com");
        email.setActivo(true);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail("test@example.com");
        dto.setActivo(true);

        when(emailRepository.findById("test@example.com")).thenReturn(Optional.of(email));
        when(emailMapper.emailToEmailDTO(email)).thenReturn(dto);

        Optional<EmailDTO> resultado = Optional.ofNullable(emailService.findById("test@example.com"));

        assertTrue(resultado.isPresent());
        assertEquals("test@example.com", resultado.get().getEmail());
        verify(emailRepository).findById("test@example.com");
        verify(emailMapper).emailToEmailDTO(email);
    }

    @Test
    void findById_emailInexistente_deberiaLanzarEntityNotFoundException() {
        when(emailRepository.findById("noexiste@example.com")).thenReturn(Optional.empty());

        jakarta.persistence.EntityNotFoundException ex = assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> emailService.findById("noexiste@example.com")
        );

        assertTrue(ex.getMessage().contains("Email no encontrado"));
        verify(emailRepository).findById("noexiste@example.com");
        verifyNoInteractions(emailMapper);
    }

    @Test
    void create_emailValido_deberiaCrearYRetornarDTO() {
        String emailInput = "newemail@example.com";
        Email emailEntity = new Email(emailInput);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(emailInput);
        dto.setActivo(true);

        when(emailRepository.findById(emailInput)).thenReturn(Optional.empty());
        when(emailRepository.save(any(Email.class))).thenReturn(emailEntity);
        when(emailMapper.emailToEmailDTO(emailEntity)).thenReturn(dto);

        EmailDTO resultado = emailService.create(emailInput);

        assertEquals(emailInput, resultado.getEmail());
        assertTrue(resultado.getActivo());
        verify(emailRepository).findById(emailInput);
        verify(emailRepository).save(any(Email.class));
        verify(emailMapper).emailToEmailDTO(emailEntity);
    }

    @Test
    void create_emailConMayusculas_deberiaGuardarNormalizado() {

        String emailInput = "TEST@EXAMPLE.COM";
        String normalizedEmail = "test@example.com";
        Email emailEntity = new Email(normalizedEmail);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(normalizedEmail);
        dto.setActivo(true);

        when(emailRepository.findById(normalizedEmail)).thenReturn(Optional.empty());
        when(emailRepository.save(any(Email.class))).thenReturn(emailEntity);
        when(emailMapper.emailToEmailDTO(emailEntity)).thenReturn(dto);

        EmailDTO resultado = emailService.create(emailInput);

        assertEquals(normalizedEmail, resultado.getEmail());
        verify(emailRepository).findById(normalizedEmail);
        verify(emailRepository).save(argThat(email -> 
            email.getEmailAddress().equals(normalizedEmail)
        ));
    }

    @Test
    void create_emailConEspacios_deberiaGuardarTrimmed() {
        String emailInput = "  spaced@example.com  ";
        String normalizedEmail = "spaced@example.com";
        Email emailEntity = new Email(normalizedEmail);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(normalizedEmail);
        dto.setActivo(true);

        when(emailRepository.findById(normalizedEmail)).thenReturn(Optional.empty());
        when(emailRepository.save(any(Email.class))).thenReturn(emailEntity);
        when(emailMapper.emailToEmailDTO(emailEntity)).thenReturn(dto);

        EmailDTO resultado = emailService.create(emailInput);

        assertEquals(normalizedEmail, resultado.getEmail());
        verify(emailRepository).findById(normalizedEmail);
    }

    @Test
    void create_emailInvalido_deberiaLanzarIllegalArgumentException() {
        String invalidEmail = "invalidemail";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.create(invalidEmail)
        );
        
        assertTrue(ex.getMessage().contains("El formato del email no es válido"));
        verifyNoInteractions(emailRepository);
    }

    @Test
    void create_emailDuplicadoActivo_deberiaRetornarExistentesSinGuardar() {
        String emailInput = "existing@example.com";
        Email existingEmail = new Email(emailInput);
        existingEmail.setActivo(true);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(emailInput);
        dto.setActivo(true);

        when(emailRepository.findById(emailInput)).thenReturn(Optional.of(existingEmail));
        when(emailMapper.emailToEmailDTO(existingEmail)).thenReturn(dto);

        EmailDTO resultado = emailService.create(emailInput);

        assertEquals(emailInput, resultado.getEmail());
        assertTrue(resultado.getActivo());
        verify(emailRepository).findById(emailInput);
        verify(emailMapper).emailToEmailDTO(existingEmail);
        verify(emailRepository, never()).save(any());
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
    }

    @Test
    void create_emailDuplicadoInactivo_deberiaReactivarYRetornar() {
        String emailInput = "reactivate@example.com";
        Email inactiveEmail = new Email(emailInput);
        inactiveEmail.setActivo(false);
        
        Email reactivatedEmail = new Email(emailInput);
        reactivatedEmail.setActivo(true);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(emailInput);
        dto.setActivo(true);

        when(emailRepository.findById(emailInput))
                .thenReturn(Optional.of(inactiveEmail))
                .thenReturn(Optional.of(reactivatedEmail));
        when(emailMapper.emailToEmailDTO(reactivatedEmail)).thenReturn(dto);

        EmailDTO resultado = emailService.create(emailInput);

        assertEquals(emailInput, resultado.getEmail());
        assertTrue(resultado.getActivo());
        verify(emailRepository, times(2)).findById(emailInput);
        verify(emailRepository).updateActivo(emailInput, true);
        verify(emailRepository, never()).save(any());
    }


    @Test
    void update_emailExistente_deberiaActualizarYRetornarDTO() {
        String emailAddress = "update@example.com";
        Email updatedEmail = new Email(emailAddress);
        updatedEmail.setActivo(true);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(emailAddress);
        dto.setActivo(true);

        when(emailRepository.existsById(emailAddress)).thenReturn(true);
        when(emailRepository.findById(emailAddress)).thenReturn(Optional.of(updatedEmail));
        when(emailMapper.emailToEmailDTO(updatedEmail)).thenReturn(dto);

        EmailDTO resultado = emailService.update(emailAddress, true);

        assertEquals(emailAddress, resultado.getEmail());
        verify(emailRepository).existsById(emailAddress);
        verify(emailRepository).updateActivo(emailAddress, true);
    }

    @Test
    void update_emailInexistente_deberiaLanzarEntityNotFoundException() {
        String emailAddress = "noexiste@example.com";

        when(emailRepository.existsById(emailAddress)).thenReturn(false);

        jakarta.persistence.EntityNotFoundException ex = assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> emailService.update(emailAddress, true)
        );

        assertTrue(ex.getMessage().contains("Destino de email no encontrado"));
        verify(emailRepository).existsById(emailAddress);
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
    }

    @Test
    void update_activoNull_deberiaNoActualizarPeroContinuar() {
        String emailAddress = "test@example.com";
        Email email = new Email(emailAddress);
        
        EmailDTO dto = new EmailDTO();
        dto.setEmail(emailAddress);
        dto.setActivo(true);

        when(emailRepository.existsById(emailAddress)).thenReturn(true);
        when(emailRepository.findById(emailAddress)).thenReturn(Optional.of(email));
        when(emailMapper.emailToEmailDTO(email)).thenReturn(dto);

        EmailDTO resultado = emailService.update(emailAddress, null);

        assertEquals(emailAddress, resultado.getEmail());
        verify(emailRepository).existsById(emailAddress);
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
        verify(emailRepository).findById(emailAddress);
    }


    @Test
    void deactivate_emailExistente_deberiaDesactivar() {
        String emailAddress = "deactivate@example.com";

        when(emailRepository.existsById(emailAddress)).thenReturn(true);

        emailService.deactivate(emailAddress);

        verify(emailRepository).existsById(emailAddress);
        verify(emailRepository).updateActivo(emailAddress, false);
    }

    @Test
    void deactivate_emailInexistente_deberiaLanzarEntityNotFoundException() {
        String emailAddress = "noexiste@example.com";

        when(emailRepository.existsById(emailAddress)).thenReturn(false);

        jakarta.persistence.EntityNotFoundException ex = assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> emailService.deactivate(emailAddress)
        );

        assertTrue(ex.getMessage().contains("Destino de email no encontrado"));
        verify(emailRepository).existsById(emailAddress);
        verify(emailRepository, never()).updateActivo(anyString(), anyBoolean());
    }


    @Test
    void findAllActiveEmails_deberiaRetornarListaDeStringsConEmails() {
        List<String> emails = List.of("admin@example.com", "user@example.com");

        when(emailRepository.findAllActiveEmails()).thenReturn(emails);

        List<String> resultado = emailService.findAllActiveEmails();

        assertEquals(2, resultado.size());
        assertEquals("admin@example.com", resultado.get(0));
        assertEquals("user@example.com", resultado.get(1));
        verify(emailRepository).findAllActiveEmails();
    }

    @Test
    void findAllActiveEmails_sinEmails_deberiaRetornarListaVacia() {
        when(emailRepository.findAllActiveEmails()).thenReturn(List.of());

        List<String> resultado = emailService.findAllActiveEmails();

        assertTrue(resultado.isEmpty());
        verify(emailRepository).findAllActiveEmails();
    }
}
