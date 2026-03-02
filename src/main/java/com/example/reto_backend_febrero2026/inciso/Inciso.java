package com.example.reto_backend_febrero2026.inciso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inciso")
public class Inciso {

    @Id
    @Column(name = "id_inciso")
    private Integer id;

    @Column(name = "nom_inciso")
    private String nombre;

    public Inciso(){}

    public Inciso(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
