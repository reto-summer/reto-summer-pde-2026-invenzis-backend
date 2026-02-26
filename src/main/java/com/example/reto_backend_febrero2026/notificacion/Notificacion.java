package com.example.reto_backend_febrero2026.notificacion;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notificacion_gen")
    @SequenceGenerator(name = "notificacion_gen", sequenceName = "notificacion_id_seq", allocationSize = 1, initialValue = 1000)
    @Column(name = "id_notificaciones", nullable = false)
    private Integer id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "exito", nullable = false)
    private Boolean exito;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion;

    public Notificacion() {
    }

    public Notificacion(String titulo, Boolean exito, String detalle, String contenido, LocalDateTime fechaEjecucion) {
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
