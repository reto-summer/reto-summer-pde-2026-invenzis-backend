package com.example.reto_backend_febrero2026.subfamilia.mapper;

import com.example.reto_backend_febrero2026.subfamilia.Subfamilia;
import com.example.reto_backend_febrero2026.subfamilia.dto.SubfamiliaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubfamiliaMapper {

    @Mapping(source = "famiCod", target = "famiCod")
    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    SubfamiliaDTO subFamilyToSubfamilyDTO(Subfamilia subfamily);

    @Mapping(source = "famiCod", target = "famiCod")
    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    Subfamilia subFamilyDTOtoSubfamily(SubfamiliaDTO subFamilyDTO);
}