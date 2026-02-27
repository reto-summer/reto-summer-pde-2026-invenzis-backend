package com.example.reto_backend_febrero2026.email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IEmailService emailService;


    @Test
    void getAllActiveEmails_deberiaRetornar200ConListaDeEmails() throws Exception {
        List<String> emails = List.of("admin@example.com", "user@example.com");
        when(emailService.findAllActiveEmails()).thenReturn(emails);

        mockMvc.perform(get("/email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("admin@example.com"))
                .andExpect(jsonPath("$[1]").value("user@example.com"));

        verify(emailService).findAllActiveEmails();
    }

    @Test
    void getAllActiveEmails_sinDatos_deberiaRetornar200ConListaVacia() throws Exception {

        when(emailService.findAllActiveEmails()).thenReturn(List.of());

        mockMvc.perform(get("/email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(emailService).findAllActiveEmails();
    }

    @Test
    void getDestinationById_emailExistente_deberiaRetornar200ConEmailDTO() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        EmailDTO dto = new EmailDTO();
        dto.setEmail("test@example.com");
        dto.setActivo(true);
        dto.setFechaCreacion(now);
        dto.setFechaActualizacion(now);

        when(emailService.findById("test@example.com")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.activo").value(true));

        verify(emailService).findById("test@example.com");
    }

    @Test
    void getDestinationById_emailInexistente_deberiaRetornar404() throws Exception {
        when(emailService.findById("noexiste@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/email/noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(emailService).findById("noexiste@example.com");
    }

    @Test
    void getDestinationById_emailConCaracteresEspeciales_deberiaRetornar200() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        EmailDTO dto = new EmailDTO();
        dto.setEmail("user+tag@example.co.uk");
        dto.setActivo(true);
        dto.setFechaCreacion(now);
        dto.setFechaActualizacion(now);

        when(emailService.findById("user+tag@example.co.uk")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/email/user+tag@example.co.uk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user+tag@example.co.uk"));

        verify(emailService).findById("user+tag@example.co.uk");
    }

    @Test
    void createDestination_emailValido_deberiaRetornar200ConMensaje() throws Exception {
        when(emailService.create("newemail@example.com")).thenReturn(new EmailDTO());

        mockMvc.perform(post("/email")
                .contentType("application/json")
                .content("{\"email\":\"newemail@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email creado exitosamente"));

        verify(emailService).create("newemail@example.com");
    }

    @Test
    void createDestination_emailConMayusculas_deberiaCrearNormalizado() throws Exception {
        EmailDTO dto = new EmailDTO();
        dto.setEmail("newemail@example.com");

        when(emailService.create("NEWEMAIL@EXAMPLE.COM")).thenReturn(dto);

        mockMvc.perform(post("/email")
                .contentType("application/json")
                .content("{\"email\":\"NEWEMAIL@EXAMPLE.COM\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email creado exitosamente"));

        verify(emailService).create("NEWEMAIL@EXAMPLE.COM");
    }

    @Test
    void createDestination_emailInvalido_deberiaRetornar400() throws Exception {
        when(emailService.create("invalidemail"))
                .thenThrow(new IllegalArgumentException("El formato del email no es válido"));

        mockMvc.perform(post("/email")
                .contentType("application/json")
                .content("{\"email\":\"invalidemail\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El formato del email no es válido"));

        verify(emailService).create("invalidemail");
    }


    @Test
    void deleteDestination_emailExistente_deberiaRetornar204() throws Exception {
        doNothing().when(emailService).deactivate("delete@example.com");

        mockMvc.perform(delete("/email/delete@example.com"))
                .andExpect(status().isNoContent());

        verify(emailService).deactivate("delete@example.com");
    }

    @Test
    void deleteDestination_emailInexistente_deberiaRetornar404() throws Exception {
        doThrow(new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, 
                "Destino de email no encontrado: noexiste@example.com"))
                .when(emailService).deactivate("noexiste@example.com");

        mockMvc.perform(delete("/email/noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(emailService).deactivate("noexiste@example.com");
    }

    @Test
    void deleteDestination_emailConCaracteresEspeciales_deberiaRetornar204() throws Exception {
        doNothing().when(emailService).deactivate("user+tag@example.co.uk");

        mockMvc.perform(delete("/email/user+tag@example.co.uk"))
                .andExpect(status().isNoContent());

        verify(emailService).deactivate("user+tag@example.co.uk");
    }
}
