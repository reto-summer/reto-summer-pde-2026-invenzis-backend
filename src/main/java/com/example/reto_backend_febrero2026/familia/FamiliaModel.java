package com.example.reto_backend_febrero2026.familia;

import jakarta.persistence.*;

@Entity
@Table(name = "familias")
public class FamiliaModel {

    @Id
    @Column(name = "cod")
    private Integer cod;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    public FamiliaModel() {
    }

    public FamiliaModel(Integer cod, String descripcion) {
        this.cod = cod;
        this.descripcion = descripcion;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}