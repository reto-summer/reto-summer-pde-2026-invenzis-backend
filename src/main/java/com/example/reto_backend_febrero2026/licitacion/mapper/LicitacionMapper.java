package com.example.reto_backend_febrero2026.licitacion.mapper;

import com.example.reto_backend_febrero2026.licitacion.Licitacion;
import com.example.reto_backend_febrero2026.licitacion.dto.LicitacionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LicitacionMapper {

    @Mapping(source = "idLicitacion", target = "idLicitacion")
    @Mapping(source = "titulo", target = "titulo")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion")
    @Mapping(source = "fechaCierre", target = "fechaCierre")
    @Mapping(source = "link", target = "link")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "subfamilia", target = "subfamilia")
    LicitacionDTO licitacionToLicitacionDTO(Licitacion licitacion);

    @Mapping(source = "idLicitacion", target = "idLicitacion")
    @Mapping(source = "titulo", target = "titulo")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion")
    @Mapping(source = "fechaCierre", target = "fechaCierre")
    @Mapping(source = "link", target = "link")
    @Mapping(source = "familia", target = "familia")
    @Mapping(source = "subfamilia", target = "subfamilia")
    Licitacion licitacionDTOtoLicitacion (LicitacionDTO licitacionDTO);


}