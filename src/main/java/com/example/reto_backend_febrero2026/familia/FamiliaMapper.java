package com.example.reto_backend_febrero2026.familia;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FamiliaMapper {

    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    FamiliaDTO familiaToFamiliaDTO(Familia family);

    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    Familia familiaDTOtoFamilia(FamiliaDTO familyDTO);
}