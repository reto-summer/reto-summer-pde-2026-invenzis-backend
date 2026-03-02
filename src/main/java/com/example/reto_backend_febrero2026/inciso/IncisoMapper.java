package com.example.reto_backend_febrero2026.inciso;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncisoMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "nombre", target = "nombre")
    IncisoDTO incisoToIncisoDTO(Inciso inciso);
}
