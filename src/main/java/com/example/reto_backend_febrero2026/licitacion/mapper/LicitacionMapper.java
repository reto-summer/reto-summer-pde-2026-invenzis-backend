package com.example.reto_backend_febrero2026.licitacion.mapper;

import com.example.reto_backend_febrero2026.licitacion.LicitacionModel;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LicitacionMapper {

    @Mapping(source = "idLicitacion", target = "idLicitacion")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion")
    @Mapping(source = "fechaCierre", target = "fechaCierre")
    @Mapping(source = "link", target = "link")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "subfamilia", target = "subfamilia")

    LicitacionModelDTO tenderToTenderDTO(LicitacionModel tender);

    @Mapping(source = "idLicitacion", target = "idLicitacion")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion")
    @Mapping(source = "fechaCierre", target = "fechaCierre")
    @Mapping(source = "link", target = "link")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "subfamilia", target = "subfamilia")

    LicitacionModel tenderDTOtoTender (LicitacionModelDTO tenderDTO);


}