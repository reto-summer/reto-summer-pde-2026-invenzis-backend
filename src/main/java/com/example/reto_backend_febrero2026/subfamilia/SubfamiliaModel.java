package com.example.reto_backend_febrero2026.subfamilia;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "subflias")
@IdClass(SubfamiliaModel.SubfamiliaId.class)
public class SubfamiliaModel {

    @Id
    @Column(name = "fami_cod")
    private Integer famiCod;

    @Id
    @Column(name = "cod")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fami_cod", insertable = false, updatable = false)
    private FamiliaModel familia;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    public SubfamiliaModel() {}

    public SubfamiliaModel(Integer famiCod, Integer cod, String descripcion) {
        this.famiCod = famiCod;
        this.cod = cod;
        this.descripcion = descripcion;
    }

    public Integer getFamiCod() {
        return famiCod;
    }

    public void setFamiCod(Integer famiCod) {
        this.famiCod = famiCod;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public FamiliaModel getFamilia() {
        return familia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public static class SubfamiliaId implements Serializable {

        private Integer famiCod;
        private Integer cod;

        public SubfamiliaId() {}

        public SubfamiliaId(Integer famiCod, Integer cod) {
            this.famiCod = famiCod;
            this.cod = cod;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SubfamiliaId)) return false;
            SubfamiliaId that = (SubfamiliaId) o;
            return Objects.equals(famiCod, that.famiCod) &&
                   Objects.equals(cod, that.cod);
        }

        @Override
        public int hashCode() {
            return Objects.hash(famiCod, cod);
        }
    }
}