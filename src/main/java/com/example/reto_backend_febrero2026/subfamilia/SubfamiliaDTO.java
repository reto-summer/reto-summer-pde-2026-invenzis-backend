package com.example.reto_backend_febrero2026.subfamilia;

public class SubfamiliaDTO {

    private Integer famiCod;

    private Integer cod;

    private String descripcion;

    public SubfamiliaDTO() {}

    public SubfamiliaDTO(Integer famiCod, Integer cod, String descripcion) {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}