package com.example.reto_backend_febrero2026.inciso;

public class IncisoDTO {

    private Integer id;
    private String nombre;

    public IncisoDTO(){}

    public IncisoDTO(Integer id, String nombre){
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
