package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.email.Email;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "licitacion_email")
public class LicitacionEmail {

    @EmbeddedId
    private LicitacionEmailId id;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @ManyToOne
    @MapsId("idLicitacion")
    @JoinColumn(name = "id_licitacion")
    private Licitacion licitacion;

    @ManyToOne
    @MapsId("email")
    @JoinColumn(name = "email")
    private Email emailEntity;

    public LicitacionEmail() {}

    public LicitacionEmail(Licitacion licitacion, Email emailEntity) {
        this.licitacion = licitacion;
        this.emailEntity = emailEntity;
        this.id = new LicitacionEmailId(licitacion.getIdLicitacion(), emailEntity.getEmailAddress());
        this.fechaEnvio = LocalDateTime.now();
    }

    public LicitacionEmailId getId() { return id; }
    public void setId(LicitacionEmailId id) { this.id = id; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public Licitacion getLicitacion() { return licitacion; }
    public void setLicitacion(Licitacion licitacion) { this.licitacion = licitacion; }

    public Email getEmailEntity() { return emailEntity; }
    public void setEmailEntity(Email emailEntity) { this.emailEntity = emailEntity; }
}
