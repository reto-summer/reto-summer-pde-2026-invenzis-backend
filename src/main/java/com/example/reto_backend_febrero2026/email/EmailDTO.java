package com.example.reto_backend_febrero2026.email;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class EmailDTO {

    private String direccionEmail;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean activo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaActualizacion;

    public EmailDTO() {}

    public String getDireccionEmail() {
        return direccionEmail;
    }

    public void setDireccionEmail(String direccionEmail) {
        this.direccionEmail = direccionEmail;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
