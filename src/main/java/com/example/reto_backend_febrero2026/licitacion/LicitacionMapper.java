package com.example.reto_backend_febrero2026.licitacion;

import com.example.reto_backend_febrero2026.integration.servlet.dto.LicitacionItemRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {LicitacionUtility.class})
public interface LicitacionMapper {

    LicitacionDTO licitacionToLicitacionDTO(Licitacion licitacion);
    Licitacion licitacionDTOtoLicitacion(LicitacionDTO licitacionDTO);


    @Mapping(target = "titulo", expression = "java(itemRecord.titulo())")
    @Mapping(target = "link", expression = "java(itemRecord.link())")

    @Mapping(source = "link", target = "idLicitacion", qualifiedByName = "extraerId")
    @Mapping(source = "descripcion", target = "descripcion", qualifiedByName = "limpiarHTML")
    @Mapping(source = "fechaPublicacion", target = "fechaPublicacion", qualifiedByName = "parsearFechaPublicacion")
    @Mapping(source = "descripcion", target = "fechaCierre", qualifiedByName = "extraerFechaCierre")
    @Mapping(source = "titulo", target = "tipoLicitacion", qualifiedByName = "extraerTipoLicitacion")

    @Mapping(target = "familia", ignore = true)
    @Mapping(target = "subfamilia", ignore = true)
    @Mapping(target = "inciso", ignore = true)
    LicitacionDTO itemRecordToDTO(LicitacionItemRecord itemRecord);
}