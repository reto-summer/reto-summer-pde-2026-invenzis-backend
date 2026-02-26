package com.example.reto_backend_febrero2026.notificacion;

import java.time.LocalDateTime;

public class NotificacionDetalleDTO {

    private Integer id;
    private String titulo;
    private Boolean exito;
    private String detalle;
    private String contenido;
    private LocalDateTime fechaEjecucion;

    public NotificacionDetalleDTO() {
    }

    public NotificacionDetalleDTO(Integer id, String titulo, Boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
        this.id = id;
        this.titulo = titulo;
        this.exito = exito;
        this.detalle = detalle;
        this.contenido = contenido;
        this.fechaEjecucion = fechaEjecucion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Boolean getExito() {
        return exito;
    }

    public void setExito(Boolean exito) {
        this.exito = exito;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }
}
