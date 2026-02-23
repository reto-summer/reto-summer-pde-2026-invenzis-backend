package com.example.reto_backend_febrero2026.familia.mapper;

import com.example.reto_backend_febrero2026.familia.FamiliaModel;
import com.example.reto_backend_febrero2026.familia.dto.FamiliaModelDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FamiliaMapper {

    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    FamiliaModelDTO familyToFamilyDTO(FamiliaModel family);

    @Mapping(source = "cod", target = "cod")
    @Mapping(source = "descripcion", target = "descripcion")
    FamiliaModel familyDTOtoFamily (FamiliaModelDTO familyDTO);

}