package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.email.Email;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "licitacion_email",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_licitacion", "email"})
        }
)
public class LicitacionEmail {

    @EmbeddedId
    private LicitacionEmailId id;

    @Column(name = "enviado", nullable = false)
    private boolean enviado = false;


    @Column(name = "fecha_envio")
    @CreationTimestamp
    private LocalDateTime fechaEnvio;

    @ManyToOne
    @MapsId("idLicitacion")
    @JoinColumn(name = "id_licitacion")
    private Licitacion licitacion;

    @ManyToOne
    @MapsId("email")
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Email emailEntity;

    public LicitacionEmail() {}

    public LicitacionEmail(Licitacion licitacion, Email emailEntity) {
        this(licitacion, emailEntity, true);
    }

    public LicitacionEmail(Licitacion licitacion, Email emailEntity, boolean enviado) {
        this.licitacion = licitacion;
        this.emailEntity = emailEntity;
        this.id = new LicitacionEmailId(licitacion.getIdLicitacion(), emailEntity.getEmailAddress());
        this.enviado = enviado;
        this.fechaEnvio = enviado ? LocalDateTime.now() : null;
    }

    public LicitacionEmailId getId() { return id; }
    public void setId(LicitacionEmailId id) { this.id = id; }

    public boolean isEnviado() { return enviado; }
    public void setEnviado(boolean enviado) { this.enviado = enviado; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public Licitacion getLicitacion() { return licitacion; }
    public void setLicitacion(Licitacion licitacion) { this.licitacion = licitacion; }

    public Email getEmailEntity() { return emailEntity; }
    public void setEmailEntity(Email emailEntity) { this.emailEntity = emailEntity; }
}
