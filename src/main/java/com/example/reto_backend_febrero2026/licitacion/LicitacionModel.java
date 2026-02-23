package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.reto_backend_febrero2026.clase.ClaseModel;
import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.subclase.SubclaseModel;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LicitacionModel {

    @JsonProperty("id_licitacion")
    private Integer idLicitacion;

    private String title;
    private String description;

    @JsonProperty("fecha_publicacion")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPublicacion;

    @JsonProperty("fecha_cierre")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCierre;

    private String link;

    private FamiliaModel familia;
    private SubfamiliaModel subfamilia;

    public LicitacionModel() {
    }

    public LicitacionModel(Integer idLicitacion, String title, String description,
                            LocalDate fechaPublicacion, LocalDateTime fechaCierre,
                            String link, FamiliaModel familia,
                            SubfamiliaModel subfamilia,
                            ClaseModel clase,
                            SubclaseModel subclase) {
        this.idLicitacion = idLicitacion;
        this.title = title;
        this.description = description;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaCierre = fechaCierre;
        this.link = link;
        this.familia = familia;
        this.subfamilia = subfamilia;
        this.clase = clase;
        this.subclase = subclase;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public FamiliaModel getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaModel familia) {
        this.familia = familia;
    }

    public SubfamiliaModel getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(SubfamiliaModel subfamilia) {
        this.subfamilia = subfamilia;
    }

    public ClaseModel getClase() {
        return clase;
    }

    public void setClase(ClaseModel clase) {
        this.clase = clase;
    }

    public SubclaseModel getSubclase() {
        return subclase;
    }

    public void setSubclase(SubclaseModel subclase) {
        this.subclase = subclase;
    }
}
