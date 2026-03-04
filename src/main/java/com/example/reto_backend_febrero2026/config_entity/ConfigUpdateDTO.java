package com.example.reto_backend_febrero2026.config_entity;

public class ConfigUpdateDTO
{
    private Integer familiaCod;
    private Integer subfamiliaCod;

    public ConfigUpdateDTO(){}

    public Integer getSubfamiliaCod() {
        return subfamiliaCod;
    }

    public void setSubfamiliaCod(Integer subfamiliaCod) {
        this.subfamiliaCod = subfamiliaCod;
    }

    public Integer getFamiliaCod() {
        return familiaCod;
    }

    public void setFamiliaCod(Integer familiaCod) {
        this.familiaCod = familiaCod;
    }
}
