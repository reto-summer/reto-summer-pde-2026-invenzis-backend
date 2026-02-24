package com.example.reto_backend_febrero2026.familia.dto;

public class FamiliaDTO {

    private Integer cod;

    private String descripcion;

    public FamiliaDTO() {
    }

    public FamiliaDTO(Integer cod, String descripcion) {
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