package com.example.reto_backend_febrero2026.subfamilia.mapper;

import com.example.reto_backend_febrero2026.subfamilia.SubfamiliaModel;
import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubfamiliaMapper {

    @Mapping(source = "famiCod", target = "famiCod")
    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "fechaBaja", target = "fechaBaja")
    @Mapping(source = "motivoBaja", target = "motivoBaja")
    SubfamiliaModelDTO subFamilyToSubfamilyDTO(SubfamiliaModel subfamily);

    @Mapping(source = "famiCod", target = "famiCod")
    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "fechaBaja", target = "fechaBaja")
    @Mapping(source = "motivoBaja", target = "motivoBaja")
    SubfamiliaModel subFamilyDTOtoSubfamily(SubfamiliaModelDTO subFamilyDTO);
}