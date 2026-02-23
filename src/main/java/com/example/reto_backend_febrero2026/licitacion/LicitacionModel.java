package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "licitacion")
public class LicitacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ver si hacer incremental o no
        @JsonProperty("id_licitacion")
    private Integer idLicitacion;

    private String title;
    private String description;

    @JsonProperty("fecha_publicacion")
    @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss Z", locale = "en")
    private OffsetDateTime fechaPublicacion;

    @JsonProperty("fecha_cierre")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaCierre;

    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familia_cod", referencedColumnName = "cod")
    private FamiliaModel familia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "subfami_fami_cod", referencedColumnName = "fami_cod"),
            @JoinColumn(name = "subfami_cod", referencedColumnName = "cod")
    })
    private com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel subfamilia;

    public LicitacionModel() {
    }

    public LicitacionModel(Integer idLicitacion, String title, String description,
                           OffsetDateTime fechaPublicacion, LocalDateTime fechaCierre,
                            String link, FamiliaModel familia,
                            com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel subfamilia) {
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

    public FamiliaModel getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaModel familia) {
        this.familia = familia;
    }

    public com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel subfamilia) {
        this.subfamilia = subfamilia;
    }
}
