package com.example.reto_backend_febrero2026.notificacion;

import java.time.LocalDateTime;

public class NotificacionResumenDTO {

    private Integer id;
    private String titulo;
    private Boolean exito;
    private LocalDateTime fechaEjecucion;

    public NotificacionResumenDTO() {
    }

    public NotificacionResumenDTO(Integer id, String titulo, Boolean exito, LocalDateTime fechaEjecucion) {
        this.id = id;
        this.titulo = titulo;
        this.exito = exito;
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

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }
}
