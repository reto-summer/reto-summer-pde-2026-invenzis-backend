package com.example.reto_backend_febrero2026.subfamilia;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubfamiliaMapper {

    @Mapping(source = "famiCod", target = "famiCod")
    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    SubfamiliaDTO subFamiliaToSubfamiliaDTO(Subfamilia subfamily);

    @Mapping(source = "famiCod", target = "famiCod")
    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    Subfamilia subFamiliaDTOtoSubfamilia(SubfamiliaDTO subFamilyDTO);
}