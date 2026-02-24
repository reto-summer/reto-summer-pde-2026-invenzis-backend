package com.example.reto_backend_febrero2026.familia;

import jakarta.persistence.*;

@Entity
@Table(name = "FAMILIAS")
public class Familia {

    @Id
    @Column(name = "COD")
    private Integer cod;

    @Column(name = "DESCRIPCION", nullable = false, length = 255)
    private String descripcion;

    public Familia() {
    }

    public Familia(Integer cod, String descripcion) {
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