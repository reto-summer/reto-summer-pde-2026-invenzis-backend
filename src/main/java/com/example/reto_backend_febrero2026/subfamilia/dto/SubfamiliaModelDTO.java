package com.example.reto_backend_febrero2026.subfamilia.dto;

import com.example.reto_backend_febrero2026.familia.dto.FamiliaModelDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class SubfamiliaModelDTO {

    private Integer famiCod;

    private Integer cod;

    private FamiliaModelDTO familia;

    private String descripcion;

    private LocalDate fechaBaja;

    private String motivoBaja;

    public SubfamiliaModelDTO() {}

    public SubfamiliaModelDTO(Integer famiCod, Integer cod, String descripcion) {
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

    public FamiliaModelDTO getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaModelDTO familia) {
        this.familia = familia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getMotivoBaja() {
        return motivoBaja;
    }

    public void setMotivoBaja(String motivoBaja) {
        this.motivoBaja = motivoBaja;
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