package com.example.reto_backend_febrero2026.config;

import com.example.reto_backend_febrero2026.familia.FamiliaDTO;
import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaDTO;

public class ConfigDTO {
    private Integer id;
    private FamiliaDTO familia;
    private SubfamiliaDTO subfamilia;

    public ConfigDTO(Integer id, FamiliaDTO familiaDTO, SubfamiliaDTO subfamiliaDTO)
    {
        this.id = id;
        this.familia = familiaDTO;
        this.subfamilia = subfamiliaDTO;
    }

    public FamiliaDTO getFamilia() {
        return familia;
    }

    public void setFamilia(FamiliaDTO familia) {
        this.familia = familia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SubfamiliaDTO getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(SubfamiliaDTO subfamilia) {
        this.subfamilia = subfamilia;
    }
}
