package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;

public class LicitacionDTO {

    private Integer idLicitacion;

    private String titulo;

    private String tipoLicitacion;

    private String descripcion;

    private OffsetDateTime fechaPublicacion;

    private LocalDateTime fechaCierre;

    private String link;

    private FamiliaDTO familia;

    private SubfamiliaDTO subfamilia;

    public LicitacionDTO() {
    }

    public LicitacionDTO(Integer idLicitacion, String titulo, String tipoLicitacion, String descripcion,
                         OffsetDateTime fechaPublicacion, LocalDateTime fechaCierre,
                         String link, FamiliaDTO familia,
                         SubfamiliaDTO subfamilia) {
        this.idLicitacion = idLicitacion;
        this.titulo = titulo;
        this.tipoLicitacion = tipoLicitacion;
        this.descripcion = descripcion;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaCierre = fechaCierre;
        this.link = link;
        this.familia = familia;
        this.subfamilia = subfamilia;
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

    public OffsetDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(OffsetDateTime fechaPublicacion) {
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

    public FamiliaDTO getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaDTO familia) {
        this.familia = familia;
    }

    public SubfamiliaDTO getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(SubfamiliaDTO subfamilia) {
        this.subfamilia = subfamilia;
    }

    public String getTipoLicitacion() { return tipoLicitacion; }

    public void setTipoLicitacion(String tipoLicitacion) { this.tipoLicitacion = tipoLicitacion; }
}
