package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;

public class LicitacionDTO {

    private Integer idLicitacion;

    private String titulo;

    private String tipoLicitacion;

    private String descripcion;

    private LocalDate fechaPublicacion;

    private LocalDateTime fechaCierre;

    private String link;

    private List<SubfamiliaDTO> subfamilias;

    public LicitacionDTO() {
    }

    public LicitacionDTO(Integer idLicitacion, String titulo, String tipoLicitacion, String descripcion,
                         LocalDate fechaPublicacion, LocalDateTime fechaCierre,
                         String link, List<SubfamiliaDTO> subfamilias) {
        this.idLicitacion = idLicitacion;
        this.titulo = titulo;
        this.tipoLicitacion = tipoLicitacion;
        this.descripcion = descripcion;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaCierre = fechaCierre;
        this.link = link;
        this.subfamilias = subfamilias;
    }

    public Integer getIdLicitacion() {
        return idLicitacion;
    }

    public void setIdLicitacion(Integer idLicitacion) {
        this.idLicitacion = idLicitacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<SubfamiliaDTO> getSubfamilias() {
        return subfamilias;
    }

    public void setSubfamilias(List<SubfamiliaDTO> subfamilias) {
        this.subfamilias = subfamilias;
    }

    public String getTipoLicitacion() { return tipoLicitacion; }

    public void setTipoLicitacion(String tipoLicitacion) { this.tipoLicitacion = tipoLicitacion; }
}
