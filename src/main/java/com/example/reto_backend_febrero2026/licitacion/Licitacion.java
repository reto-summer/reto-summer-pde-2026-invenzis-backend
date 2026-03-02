package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.inciso.Inciso;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "licitacion")
public class Licitacion {

    @Id
    @Column(name = "id_licitacion")
    private Integer idLicitacion;

    @Column(name = "titulo", columnDefinition = "TEXT")
    private String titulo;

    @Column(name = "tipo_licitacion", columnDefinition = "TEXT")
    private String tipoLicitacion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en")
    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "link")
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inciso", referencedColumnName = "id_inciso")
    private Inciso inciso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familia_cod", referencedColumnName = "cod")
    private Familia familia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "subfami_fami_cod", referencedColumnName = "fami_cod"),
            @JoinColumn(name = "subfami_cod", referencedColumnName = "cod")
    })
    private Subfamilia subfamilia;

    public Licitacion() {
    }

    public Licitacion(Integer idLicitacion, String titulo, String tipoLicitacion, String descripcion,
                      LocalDate fechaPublicacion, LocalDateTime fechaCierre,
                      String link, Familia familia, Subfamilia subfamilia, Inciso inciso) {
        this.idLicitacion = idLicitacion;
        this.titulo = titulo;
        this.tipoLicitacion = tipoLicitacion;
        this.descripcion = descripcion;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaCierre = fechaCierre;
        this.link = link;
        this.familia = familia;
        this.subfamilia = subfamilia;
        this.inciso = inciso;
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

    public Inciso getInciso() {
        return inciso;
    }

    public void setInciso(Inciso inciso) {
        this.inciso = inciso;
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

    public String getTipoLicitacion() { return tipoLicitacion; }

    public void setTipoLicitacion(String tipoLicitacion) { this.tipoLicitacion = tipoLicitacion; }

}
