package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "LICITACION")
public class Licitacion {

    @Id
    @JsonProperty("ID_LICITACION")
    @Column(name = "ID_LICITACION")
    private Integer idLicitacion;
    @Column(name = "TITULO", columnDefinition = "TEXT")
    private String titulo;
    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @JsonProperty("fecha_publicacion")
    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en")
    @Column(name = "FECHA_PUBLICACION")
    private OffsetDateTime fechaPublicacion;

    @JsonProperty("fecha_cierre")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre;

    @Column(name = "LINK")
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAMILIA_COD", referencedColumnName = "COD")
    private Familia familia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SUBFAMI_FAMI_COD", referencedColumnName = "FAMI_COD"),
            @JoinColumn(name = "SUBFAMI_COD", referencedColumnName = "COD")
    })
    private Subfamilia subfamilia;

    public Licitacion() {
    }

    public Licitacion(Integer idLicitacion, String titulo, String descripcion, OffsetDateTime fechaPublicacion, LocalDateTime fechaCierre,
                      String link, Familia familia, Subfamilia subfamilia) {
        this.idLicitacion = idLicitacion;
        this.titulo = titulo;
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

    public Familia getFamilia() {
        return familia;
    }

    public void setFamilia(Familia familia) {
        this.familia = familia;
    }

    public Subfamilia getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(Subfamilia subfamilia) {
        this.subfamilia = subfamilia;
    }
}
