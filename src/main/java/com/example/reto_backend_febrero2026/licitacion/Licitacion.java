package com.example.reto_backend_febrero2026.licitacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.reto_backend_febrero2026.familia.Familia;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "licitacion_subfamilia",
            joinColumns = @JoinColumn(name = "licitacion_id", referencedColumnName = "id_licitacion"),
            inverseJoinColumns = {
                    @JoinColumn(name = "subfami_cod", referencedColumnName = "cod"),
                    @JoinColumn(name = "fami_cod", referencedColumnName = "fami_cod")
            }
    )
    private List<Subfamilia> subfamilias;


    public Licitacion() {
    }

    public Licitacion(Integer idLicitacion, String titulo, String tipoLicitacion, String descripcion,
                      LocalDate fechaPublicacion, LocalDateTime fechaCierre,
                      String link, List<Subfamilia> subfamilias) {
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

    public List<Subfamilia> getSubfamilias() {
        return subfamilias;
    }

    public void setSubfamilias(List<Subfamilia> subfamilias) {
        this.subfamilias = subfamilias;
    }

    public String getTipoLicitacion() { return tipoLicitacion; }

    public void setTipoLicitacion(String tipoLicitacion) { this.tipoLicitacion = tipoLicitacion; }

}
