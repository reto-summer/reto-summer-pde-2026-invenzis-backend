package com.example.reto_backend_febrero2026.notificacion;

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

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private INotificacionService notificacionService;

    @Test
    void getAll_conVariasNotificaciones_deberiaRetornar200ConLista() throws Exception {
        LocalDateTime fecha1 = LocalDateTime.of(2026, 2, 27, 10, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 2, 27, 11, 0);

        NotificacionResumenDTO dto1 = new NotificacionResumenDTO(1, "Notif 1", true, fecha1);
        NotificacionResumenDTO dto2 = new NotificacionResumenDTO(2, "Notif 2", false, fecha2);

        List<NotificacionResumenDTO> notificaciones = List.of(dto1, dto2);

        when(notificacionService.findAllResumen()).thenReturn(notificaciones);

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Notif 1"))
                .andExpect(jsonPath("$[0].exito").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Notif 2"))
                .andExpect(jsonPath("$[1].exito").value(false));

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getAll_sinNotificaciones_deberiaRetornar200ConListaVacia() throws Exception {
        when(notificacionService.findAllResumen()).thenReturn(List.of());

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getAll_conNotificacionesExitosas_deberiaMostrarSoloExitoTrue() throws Exception {
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        NotificacionResumenDTO dto = new NotificacionResumenDTO(1, "Exitosa", true, fecha);

        when(notificacionService.findAllResumen()).thenReturn(List.of(dto));

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exito").value(true));

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getById_conIdExistente_deberiaRetornar200ConDetalle() throws Exception {
        Integer id = 1;
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 30);

        NotificacionDetalleDTO dto = new NotificacionDetalleDTO(
            id,
            "Test Notification",
            true,
            "Detalle completo",
            "Contenido HTML",
            fecha
        );

        when(notificacionService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Test Notification"))
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.detalle").value("Detalle completo"))
                .andExpect(jsonPath("$.contenido").value("Contenido HTML"));

        verify(notificacionService).findById(id);
    }

    @Test
    void getById_conIdInexistente_deberiaRetornar404() throws Exception {
        Integer id = 999;
        when(notificacionService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isNotFound());

        verify(notificacionService).findById(id);
    }

    @Test
    void getById_conIdCero_deberiaRetornar404() throws Exception {
        Integer id = 0;
        when(notificacionService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isNotFound());

        verify(notificacionService).findById(id);
    }

    @Test
    void getById_conNotificacionSinDetalleNiContenido_deberiaRetornar200ConNulls() throws Exception {
        Integer id = 5;
        LocalDateTime fecha = LocalDateTime.now();

        NotificacionDetalleDTO dto = new NotificacionDetalleDTO(
            id,
            "Simple Notification",
            true,
            null,
            null,
            fecha
        );

        when(notificacionService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Simple Notification"))
                .andExpect(jsonPath("$.detalle").isEmpty())
                .andExpect(jsonPath("$.contenido").isEmpty());

        verify(notificacionService).findById(id);
    }

    @Test
    void getById_conNotificacionFallida_deberiaRetornar200ConExitoFalse() throws Exception {
        Integer id = 10;
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 15, 45);

        NotificacionDetalleDTO dto = new NotificacionDetalleDTO(
            id,
            "Failed Notification",
            false,
            "Error details",
            "Error stack trace",
            fecha
        );

        when(notificacionService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.detalle").value("Error details"))
                .andExpect(jsonPath("$.contenido").value("Error stack trace"));

        verify(notificacionService).findById(id);
    }

    @Test
    void getAll_verificarFormatoFecha_deberiaSerializarCorrectamente() throws Exception {
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 14, 30, 15);
        NotificacionResumenDTO dto = new NotificacionResumenDTO(1, "Test", true, fecha);

        when(notificacionService.findAllResumen()).thenReturn(List.of(dto));

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fechaEjecucion").exists());

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getById_conIdNegativo_deberiaRetornar404() throws Exception {
        Integer id = -1;
        when(notificacionService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isNotFound());

        verify(notificacionService).findById(id);
    }

    @Test
    void getById_conIdMuyGrande_deberiaConsultarCorrectamente() throws Exception {
        Integer id = Integer.MAX_VALUE;
        when(notificacionService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isNotFound());

        verify(notificacionService).findById(id);
    }

    @Test
    void getAll_conTitulosEspeciales_deberiaSerializarCorrectamente() throws Exception {
        LocalDateTime fecha = LocalDateTime.now();
        NotificacionResumenDTO dto1 = new NotificacionResumenDTO(1, "Título con ñ y acentos: áéíóú", true, fecha);
        NotificacionResumenDTO dto2 = new NotificacionResumenDTO(2, "Title with \"quotes\"", true, fecha);
        NotificacionResumenDTO dto3 = new NotificacionResumenDTO(3, "Título con <html> tags", false, fecha);

        when(notificacionService.findAllResumen()).thenReturn(List.of(dto1, dto2, dto3));

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Título con ñ y acentos: áéíóú"))
                .andExpect(jsonPath("$[1].titulo").value("Title with \"quotes\""))
                .andExpect(jsonPath("$[2].titulo").value("Título con <html> tags"));

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getById_conContenidoHTML_deberiaRetornarSinEscapar() throws Exception {
        Integer id = 1;
        LocalDateTime fecha = LocalDateTime.now();
        String contenidoHTML = "<html><body><h1>Test</h1></body></html>";

        NotificacionDetalleDTO dto = new NotificacionDetalleDTO(
            id,
            "HTML Notification",
            true,
            "Detalle",
            contenidoHTML,
            fecha
        );

        when(notificacionService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value(contenidoHTML));

        verify(notificacionService).findById(id);
    }

    @Test
    void getAll_verificarCORSHeaders_deberiaPermitirCrossOrigin() throws Exception {
        when(notificacionService.findAllResumen()).thenReturn(List.of());

        mockMvc.perform(get("/notificacion")
                .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk());

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getById_verificarCORSHeaders_deberiaPermitirCrossOrigin() throws Exception {
        Integer id = 1;
        when(notificacionService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/notificacion/{id}", id)
                .header("Origin", "http://localhost:4200"))
                .andExpect(status().isNotFound());

        verify(notificacionService).findById(id);
    }

    @Test
    void getAll_conUnaNotificacion_deberiaRetornarArrayConUnElemento() throws Exception {
        LocalDateTime fecha = LocalDateTime.now();
        NotificacionResumenDTO dto = new NotificacionResumenDTO(1, "Única", true, fecha);

        when(notificacionService.findAllResumen()).thenReturn(List.of(dto));

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(notificacionService).findAllResumen();
    }

    @Test
    void getById_conNotificacionCompleta_deberiaTenerTodosLosCampos() throws Exception {
        Integer id = 1;
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 30, 45);

        NotificacionDetalleDTO dto = new NotificacionDetalleDTO(
            id,
            "Complete Notification",
            true,
            "Detailed information about the execution",
            "<html><body>Email content</body></html>",
            fecha
        );

        when(notificacionService.findById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/notificacion/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").exists())
                .andExpect(jsonPath("$.exito").exists())
                .andExpect(jsonPath("$.detalle").exists())
                .andExpect(jsonPath("$.contenido").exists())
                .andExpect(jsonPath("$.fechaEjecucion").exists());

        verify(notificacionService).findById(id);
    }

    @Test
    void getAll_conMuchasNotificaciones_deberiaRetornarTodasCorrectamente() throws Exception {
        LocalDateTime fecha = LocalDateTime.now();
        List<NotificacionResumenDTO> notificaciones = List.of(
            new NotificacionResumenDTO(1, "Notif 1", true, fecha),
            new NotificacionResumenDTO(2, "Notif 2", false, fecha),
            new NotificacionResumenDTO(3, "Notif 3", true, fecha),
            new NotificacionResumenDTO(4, "Notif 4", true, fecha),
            new NotificacionResumenDTO(5, "Notif 5", false, fecha)
        );

        when(notificacionService.findAllResumen()).thenReturn(notificaciones);

        mockMvc.perform(get("/notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[4].id").value(5));

        verify(notificacionService).findAllResumen();
    }
}
