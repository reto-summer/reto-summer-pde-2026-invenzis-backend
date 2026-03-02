package com.example.reto_backend_febrero2026.licitacion_email;

import com.example.reto_backend_febrero2026.channel.email.Email;
import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "licitacion_email")
@IdClass(LicitacionEmail.LicitacionEmailId.class)
public class LicitacionEmail {

    @Id
    @Column(name = "id_licitacion")
    private Integer idLicitacion;

    @Id
    @Column(name = "email")
    private String direccionEmail;

    @Column(name = "enviado", nullable = false)
    private boolean enviado = false;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_licitacion", insertable = false, updatable = false)
    private Licitacion licitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", insertable = false, updatable = false)
    private Email email;

    public LicitacionEmail() {}

    public LicitacionEmail(Licitacion licitacion, Email email) {
        setLicitacion(licitacion);
        setEmail(email);
    }

    public void marcarComoEnviado() {
        this.enviado = true;
        this.fechaEnvio = LocalDateTime.now();
    }

    public Integer getIdLicitacion() {
        return idLicitacion;
    }

    public String getDireccionEmail() {
        return direccionEmail;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public Licitacion getLicitacion() {
        return licitacion;
    }

    public Email getEmail() {
        return email;
    }

    public void setLicitacion(Licitacion licitacion) {
        this.licitacion = licitacion;

        if (licitacion != null) {
            this.idLicitacion = licitacion.getIdLicitacion();
        }
        else {
            this.idLicitacion = null;
        }
    }

    public void setEmail(Email email) {
        this.email = email;

        if (email != null) {
            this.direccionEmail = email.getDireccionEmail();
        }
        else {
            this.direccionEmail = null;
        }
    }

    public static class LicitacionEmailId implements Serializable {

        private Integer idLicitacion;
        private String direccionEmail;

        public LicitacionEmailId() {}

        public LicitacionEmailId(Integer idLicitacion, String direccionEmail) {
            this.idLicitacion = idLicitacion;
            this.direccionEmail = direccionEmail;
        }

        public Integer getIdLicitacion() {
            return idLicitacion;
        }

        public void setIdLicitacion(Integer idLicitacion) {
            this.idLicitacion = idLicitacion;
        }

        public String getDireccionEmail() {
            return direccionEmail;
        }

        public void setDireccionEmail(String direccionEmail) {
            this.direccionEmail = direccionEmail;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LicitacionEmailId that)) return false;
            return Objects.equals(idLicitacion, that.idLicitacion)
                    && Objects.equals(direccionEmail, that.direccionEmail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idLicitacion, direccionEmail);
        }
    }
}

