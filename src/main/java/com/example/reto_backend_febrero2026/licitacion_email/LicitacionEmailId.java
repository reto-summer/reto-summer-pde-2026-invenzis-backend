package com.example.reto_backend_febrero2026.licitacion_email;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LicitacionEmailId implements Serializable {

    @Column(name = "id_licitacion")
    private Integer idLicitacion;

    @Column(name = "email")
    private String email;

    public LicitacionEmailId() {}

    public LicitacionEmailId(Integer idLicitacion, String email) {
        this.idLicitacion = idLicitacion;
        this.email = email;
    }

    public Integer getIdLicitacion() { return idLicitacion; }
    public void setIdLicitacion(Integer idLicitacion) { this.idLicitacion = idLicitacion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicitacionEmailId that)) return false;
        return Objects.equals(idLicitacion, that.idLicitacion) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLicitacion, email);
    }
}
