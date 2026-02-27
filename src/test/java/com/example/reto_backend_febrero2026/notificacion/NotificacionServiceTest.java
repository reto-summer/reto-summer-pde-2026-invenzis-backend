package com.example.reto_backend_febrero2026.notificacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private INotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    void create_conDatosValidos_deberiaCrearYRetornarNotificacion() {
        String titulo = "Test Notification";
        boolean exito = true;
        String detalle = "Test details";
        String contenido = "Test content";
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 30);

        Notificacion notificacionGuardada = new Notificacion(titulo, exito, detalle, contenido, fecha);
        notificacionGuardada.setId(1);

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionGuardada);

        Notificacion resultado = notificacionService.create(titulo, exito, detalle, contenido, fecha);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(exito, resultado.getExito());
        assertEquals(detalle, resultado.getDetalle());
        assertEquals(contenido, resultado.getContenido());
        assertEquals(fecha, resultado.getFechaEjecucion());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void create_conExitoFalse_deberiaCrearNotificacionFallida() {
        String titulo = "Failed Notification";
        boolean exito = false;
        String detalle = "Error occurred";
        String contenido = "Error content";
        LocalDateTime fecha = LocalDateTime.now();

        Notificacion notificacionGuardada = new Notificacion(titulo, exito, detalle, contenido, fecha);
        notificacionGuardada.setId(2);

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionGuardada);

        Notificacion resultado = notificacionService.create(titulo, exito, detalle, contenido, fecha);

        assertNotNull(resultado);
        assertFalse(resultado.getExito());
        assertEquals(titulo, resultado.getTitulo());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void create_conDetalleYContenidoNull_deberiaCrearNotificacion() {
        String titulo = "Simple Notification";
        boolean exito = true;
        LocalDateTime fecha = LocalDateTime.now();

        Notificacion notificacionGuardada = new Notificacion(titulo, exito, null, null, fecha);
        notificacionGuardada.setId(3);

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionGuardada);

        Notificacion resultado = notificacionService.create(titulo, exito, null, null, fecha);

        assertNotNull(resultado);
        assertEquals(titulo, resultado.getTitulo());
        assertNull(resultado.getDetalle());
        assertNull(resultado.getContenido());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void findAllResumen_conVariasNotificaciones_deberiaRetornarListaDeResumenDTO() {
        LocalDateTime fecha1 = LocalDateTime.of(2026, 2, 27, 10, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 2, 27, 11, 0);

        Notificacion notif1 = new Notificacion("Notif 1", true, "detalle1", "contenido1", fecha1);
        notif1.setId(1);

        Notificacion notif2 = new Notificacion("Notif 2", false, "detalle2", "contenido2", fecha2);
        notif2.setId(2);

        List<Notificacion> notificaciones = List.of(notif1, notif2);

        when(notificacionRepository.findAll()).thenReturn(notificaciones);

        List<NotificacionResumenDTO> resultado = notificacionService.findAllResumen();

        assertEquals(2, resultado.size());
        
        NotificacionResumenDTO dto1 = resultado.get(0);
        assertEquals(1, dto1.getId());
        assertEquals("Notif 1", dto1.getTitulo());
        assertTrue(dto1.getExito());
        assertEquals(fecha1, dto1.getFechaEjecucion());

        NotificacionResumenDTO dto2 = resultado.get(1);
        assertEquals(2, dto2.getId());
        assertEquals("Notif 2", dto2.getTitulo());
        assertFalse(dto2.getExito());
        assertEquals(fecha2, dto2.getFechaEjecucion());

        verify(notificacionRepository).findAll();
    }

    @Test
    void findAllResumen_sinNotificaciones_deberiaRetornarListaVacia() {
        when(notificacionRepository.findAll()).thenReturn(List.of());

        List<NotificacionResumenDTO> resultado = notificacionService.findAllResumen();

        assertTrue(resultado.isEmpty());
        verify(notificacionRepository).findAll();
    }

    @Test
    void findById_conIdExistente_deberiaRetornarOptionalConDetalleDTO() {
        Integer id = 1;
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 10, 0);
        
        Notificacion notificacion = new Notificacion("Test", true, "Detalle test", "Contenido test", fecha);
        notificacion.setId(id);

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));

        Optional<NotificacionDetalleDTO> resultado = notificacionService.findById(id);

        assertTrue(resultado.isPresent());
        
        NotificacionDetalleDTO dto = resultado.get();
        assertEquals(id, dto.getId());
        assertEquals("Test", dto.getTitulo());
        assertTrue(dto.getExito());
        assertEquals("Detalle test", dto.getDetalle());
        assertEquals("Contenido test", dto.getContenido());
        assertEquals(fecha, dto.getFechaEjecucion());

        verify(notificacionRepository).findById(id);
    }

    @Test
    void findById_conIdInexistente_deberiaRetornarOptionalVacio() {
        Integer id = 999;
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());

        Optional<NotificacionDetalleDTO> resultado = notificacionService.findById(id);

        assertFalse(resultado.isPresent());
        verify(notificacionRepository).findById(id);
    }

    @Test
    void findById_conNotificacionSinDetalleNiContenido_deberiaRetornarDTOConNulls() {
        Integer id = 1;
        LocalDateTime fecha = LocalDateTime.now();
        
        Notificacion notificacion = new Notificacion("Test", true, null, null, fecha);
        notificacion.setId(id);

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));

        Optional<NotificacionDetalleDTO> resultado = notificacionService.findById(id);

        assertTrue(resultado.isPresent());
        NotificacionDetalleDTO dto = resultado.get();
        assertNull(dto.getDetalle());
        assertNull(dto.getContenido());
        verify(notificacionRepository).findById(id);
    }

    @Test
    void findExitosas_conNotificacionesExitosas_deberiaRetornarSoloExitosas() {
        LocalDateTime fecha1 = LocalDateTime.of(2026, 2, 27, 10, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 2, 27, 11, 0);

        Notificacion notif1 = new Notificacion("Exitosa 1", true, "detalle1", "contenido1", fecha1);
        notif1.setId(1);

        Notificacion notif2 = new Notificacion("Exitosa 2", true, "detalle2", "contenido2", fecha2);
        notif2.setId(2);

        List<Notificacion> notificacionesExitosas = List.of(notif1, notif2);

        when(notificacionRepository.findByExitoTrue()).thenReturn(notificacionesExitosas);

        List<NotificacionResumenDTO> resultado = notificacionService.findExitosas();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(NotificacionResumenDTO::getExito));
        assertEquals("Exitosa 1", resultado.get(0).getTitulo());
        assertEquals("Exitosa 2", resultado.get(1).getTitulo());
        verify(notificacionRepository).findByExitoTrue();
    }

    @Test
    void findExitosas_sinNotificacionesExitosas_deberiaRetornarListaVacia() {
        when(notificacionRepository.findByExitoTrue()).thenReturn(List.of());

        List<NotificacionResumenDTO> resultado = notificacionService.findExitosas();

        assertTrue(resultado.isEmpty());
        verify(notificacionRepository).findByExitoTrue();
    }

    @Test
    void findFallidas_conNotificacionesFallidas_deberiaRetornarSoloFallidas() {
        LocalDateTime fecha1 = LocalDateTime.of(2026, 2, 27, 10, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 2, 27, 11, 0);

        Notificacion notif1 = new Notificacion("Fallida 1", false, "error1", "contenido1", fecha1);
        notif1.setId(1);

        Notificacion notif2 = new Notificacion("Fallida 2", false, "error2", "contenido2", fecha2);
        notif2.setId(2);

        List<Notificacion> notificacionesFallidas = List.of(notif1, notif2);

        when(notificacionRepository.findByExitoFalse()).thenReturn(notificacionesFallidas);

        List<NotificacionResumenDTO> resultado = notificacionService.findFallidas();


        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().noneMatch(NotificacionResumenDTO::getExito));
        assertEquals("Fallida 1", resultado.get(0).getTitulo());
        assertEquals("Fallida 2", resultado.get(1).getTitulo());
        verify(notificacionRepository).findByExitoFalse();
    }

    @Test
    void findFallidas_sinNotificacionesFallidas_deberiaRetornarListaVacia() {
        when(notificacionRepository.findByExitoFalse()).thenReturn(List.of());

        List<NotificacionResumenDTO> resultado = notificacionService.findFallidas();

        assertTrue(resultado.isEmpty());
        verify(notificacionRepository).findByExitoFalse();
    }

    @Test
    void findAllResumen_deberiaMapearCorrectamenteLosCampos() {
        LocalDateTime fecha = LocalDateTime.of(2026, 2, 27, 15, 30, 45);
        
        Notificacion notificacion = new Notificacion(
            "Título específico", 
            true, 
            "Detalle no mapeado", 
            "Contenido no mapeado", 
            fecha
        );
        notificacion.setId(100);

        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));

        List<NotificacionResumenDTO> resultado = notificacionService.findAllResumen();

        assertEquals(1, resultado.size());
        NotificacionResumenDTO dto = resultado.get(0);

        assertEquals(100, dto.getId());
        assertEquals("Título específico", dto.getTitulo());
        assertTrue(dto.getExito());
        assertEquals(fecha, dto.getFechaEjecucion());
        
        verify(notificacionRepository).findAll();
    }

    @Test
    void findExitosas_deberiaMapearCorrectamenteFechasComplejas() {
        LocalDateTime fechaConMilisegundos = LocalDateTime.of(2026, 2, 27, 23, 59, 59, 999999999);
        
        Notificacion notificacion = new Notificacion("Test", true, "detalle", "contenido", fechaConMilisegundos);
        notificacion.setId(1);

        when(notificacionRepository.findByExitoTrue()).thenReturn(List.of(notificacion));

        List<NotificacionResumenDTO> resultado = notificacionService.findExitosas();

        assertEquals(1, resultado.size());
        assertEquals(fechaConMilisegundos, resultado.get(0).getFechaEjecucion());
        verify(notificacionRepository).findByExitoTrue();
    }

    @Test
    void create_conTituloLargo_deberiaGuardarCorrectamente() {
        String tituloLargo = "Este es un título muy largo que podría tener muchos caracteres para probar el límite del campo";
        LocalDateTime fecha = LocalDateTime.now();

        Notificacion notificacionGuardada = new Notificacion(tituloLargo, true, "detalle", "contenido", fecha);
        notificacionGuardada.setId(1);

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionGuardada);

        Notificacion resultado = notificacionService.create(tituloLargo, true, "detalle", "contenido", fecha);

        assertNotNull(resultado);
        assertEquals(tituloLargo, resultado.getTitulo());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void findById_conMultiplesLlamadasAlMismoId_deberiaConsultarCadaVez() {
        Integer id = 1;
        LocalDateTime fecha = LocalDateTime.now();
        Notificacion notificacion = new Notificacion("Test", true, "detalle", "contenido", fecha);
        notificacion.setId(id);

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));

        notificacionService.findById(id);
        notificacionService.findById(id);

        verify(notificacionRepository, times(2)).findById(id);
    }
}
