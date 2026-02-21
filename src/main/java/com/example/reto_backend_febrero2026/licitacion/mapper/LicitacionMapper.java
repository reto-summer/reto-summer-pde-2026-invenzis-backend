package com.example.reto_backend_febrero2026.licitacion.mapper;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LicitacionMapper {

    LicitacionMapper INSTANCE = Mappers.getMapper(LicitacionMapper.class);

    @Mapping(source = "idLicitacion", target = "idLicitacion")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion")
    @Mapping(source = "fechaCierre", target = "fechaCierre")
    @Mapping(source = "link", target = "link")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "subfamilia", target = "subfamilia")
    @Mapping(source = "clase", target = "clase")
    @Mapping(source = "subclase", target = "subclase")
    LicitacionModelDTO tenderToTenderDTO(LicitacionModel tender);

    @Mapping(source = "idLicitacion", target = "idLicitacion")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion")
    @Mapping(source = "fechaCierre", target = "fechaCierre")
    @Mapping(source = "link", target = "link")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "subfamilia", target = "subfamilia")
    @Mapping(source = "clase", target = "clase")
    @Mapping(source = "subclase", target = "subclase")
    LicitacionModel tenderDTOtoTender (LicitacionModelDTO tenderDTO);


}