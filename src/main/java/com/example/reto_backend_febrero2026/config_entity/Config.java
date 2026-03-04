package com.example.reto_backend_febrero2026.config_entity;

import com.example.reto_backend_febrero2026.familia.Familia;
import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import jakarta.persistence.*;

@Entity
@Table(name = "config")
public class Config
{
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "familia_cod", referencedColumnName = "cod")
    private Familia familia;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "subfami_fami_cod", referencedColumnName = "fami_cod"),
            @JoinColumn(name = "subfami_cod", referencedColumnName = "cod")
    })
    private Subfamilia subfamilia;

    public Config() {}

    public Config(Integer id, Familia familia, Subfamilia subfamilia)
    {
        this.familia = familia;
        this.subfamilia = subfamilia;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Familia getFamilia() {
        return familia;
    }

    public void setFamilia(Familia familia) {
        this.familia = familia;
    }

    public Subfamilia getSubfamilia() {
        return subfamilia;
    }

    public void setSubfamilia(Subfamilia subfamilia) {
        this.subfamilia = subfamilia;
    }
}
