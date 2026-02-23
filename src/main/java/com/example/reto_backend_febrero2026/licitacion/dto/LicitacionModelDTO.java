package com.example.reto_backend_febrero2026.licitacion.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.dto.FamiliaModelDTO;
import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LicitacionModelDTO {

    @JsonProperty("id_licitacion")
    private Integer idLicitacion;

    private String title;
    private String description;

    @JsonProperty("fecha_publicacion")
    private OffsetDateTime fechaPublicacion;

    @JsonProperty("fecha_cierre")
    private LocalDateTime fechaCierre;

    private String link;

    private FamiliaModelDTO familia;

    private SubfamiliaModelDTO subfamilia;

    public LicitacionModelDTO() {
    }

    public LicitacionModelDTO(Integer idLicitacion, String title, String description,
                              OffsetDateTime fechaPublicacion, LocalDateTime fechaCierre,
                           String link, FamiliaModelDTO familia,
                              SubfamiliaModelDTO subfamilia) {
        this.idLicitacion = idLicitacion;
        this.title = title;
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public FamiliaModelDTO getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaModelDTO familia) {
        this.familia = familia;
    }

    public SubfamiliaModelDTO getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(SubfamiliaModelDTO subfamilia) {
        this.subfamilia = subfamilia;
    }
}
