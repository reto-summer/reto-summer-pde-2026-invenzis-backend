--Familias
insert into familias(cod, descripcion, comprable)
values (10, 'BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION', 'S');
insert into familias(cod, descripcion, comprable)
values (12, 'PRODUCTOS EXCLUIDOS DEL CATALOGO UNICO DE BIENES Y SERVICIOS', 'S');
insert into familias(cod, descripcion, comprable)
values (11, 'OTROS GASTOS', 'N');
insert into familias(cod, descripcion, comprable)
values (2, 'MATERIALES Y SUMINISTROS', 'S');
insert into familias(cod, descripcion, comprable)
values (3, 'SERVICIOS NO PERSONALES', 'S');
insert into familias(cod, descripcion, comprable)
values (4, 'MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS', 'S');
insert into familias(cod, descripcion, comprable)
values (5, 'TIERRAS, EDIFICIOS Y OTROS BIENES DE USO', 'S');
insert into familias(cod, descripcion, comprable)
values (6, 'CONSTRUCCIONES, MEJORAS Y REPARACIONES EXTRAORDINARIAS', 'S');
insert into familias(cod, descripcion, comprable)
values (1, 'SERVICIOS PERSONALES', 'N');

-- SUBFAMILIAS
insert into subfamilias(fami_cod, cod, descripcion)
values (10, 43, 'INFRAESTRUCTURA TECNOLOGICA');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 10, 'SERVICIOS DE TECNOLOGIAS DE LA INFORMACION Y COMUNICACION');
insert into subfamilias(fami_cod, cod, descripcion)
values (12, 2, 'PRODUCTOS DE USO EXCLUSIVO DE ANCAP, SIN CRITERIO ARCE');
insert into subfamilias(fami_cod, cod, descripcion)
values (12, 3, 'PRODUCTOS DE USO EXCLUSIVO DE ANTEL, SIN CRITERIO ARCE');
insert into subfamilias(fami_cod, cod, descripcion)
values (1, 1, 'SERVICIOS PERSONALES');
insert into subfamilias(fami_cod, cod, descripcion)
values (11, 1, 'OTROS GASTOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (12, 1, 'PRODUCTOS DE USO EXCLUSIVOS DE UTE, SIN CRITERIOS ARCE');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 1, 'ALIMENTOS Y PRODUCTOS AGROPECUARIOS, FORESTALES Y MARITIMOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 1, 'SERVICIOS BASICOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 2, 'MINERALES');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 3, 'PRODUCTOS TEXTILES, DE VESTIR Y CUERO');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 4, 'PRODUCTOS DE PAPEL, LIBROS, DOCUMENTOS  IMPRESOS Y DIGITALES');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 5, 'PRODUCTOS ENERGETICOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 6, 'PROD. QUIMICOS Y CONEXOS EXCEPTO MEDIC.Y ANTISEP. USO HUMANO');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 7, 'PRODUCTOS MINERALES NO METALICOS Y DE MADERA');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 8, 'PRODUCTOS METALICOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 9, 'OTROS MATERIALES Y SUMINISTROS');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 2, 'PUBLICIDAD, IMPRESIONES Y ENCUADERNACIONES');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 1, 'MAQUINARIAS Y EQUIPOS DE PRODUCCION');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 2, 'MAQUINAS Y EQUIPOS DE OFICINA Y SIMILARES');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 3, 'PASAJES, VIATICOS Y OTROS GASTOS DE TRASLADO');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 3, 'EQUIPOS MEDICOS, SANITARIOS, ODONTOLOGICOS Y CIENTIFICOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 4, 'TRANSPORTE Y ALMACENAJE');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 4, 'EQUIPOS EDUCACIONALES, CULTURALES Y RECREATIVOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 5, 'ARRENDAMIENTOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 5, 'EQUIPOS DE TRANSPORTE');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 6, 'EQUIPOS DE COMUNICACIONES EXCEPTO TELEFONIA');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 7, 'MOTORES Y PARTES PARA REEMPLAZO');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 8, 'MOBILIARIO');
insert into subfamilias(fami_cod, cod, descripcion)
values (4, 9, 'OTRAS MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (5, 1, 'TIERRAS');
insert into subfamilias(fami_cod, cod, descripcion)
values (5, 2, 'EDIFICIOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (5, 3, 'MAQUINAS, EQUIPOS Y MOBILIARIO EXISTENTE');
insert into subfamilias(fami_cod, cod, descripcion)
values (5, 5, 'ACTIVOS FINANCIEROS');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 1, 'VIAS DE COMUNICACION');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 2, 'EDIFICACIONES');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 3, 'OBRAS HIDRAULICAS, HIDROELECTRICAS, ELECTRICAS Y SANITARIAS');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 4, 'OBRAS URBANISTICAS');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 5, 'INSTALACIONES DE TRASMISION Y DISTRIBUCION');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 6, 'MEJORAS DE TIERRAS Y PLANTACIONES');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 7, 'PERFORACIONES Y EXPLORACIONES MINERAS');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 8, 'REPARACIONES MAYORES Y EXTRAORDINARIAS');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 6, 'TRIBUTOS, MULTAS, RECARGOS, SEGUROS Y COMISIONES');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 7, 'SERVICIOS CONTRATADOS PARA MANTENIM.Y REPARACIONES MENORES');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 8, 'SERVICIOS PROFESIONALES CONTRATADOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (3, 9, 'OTROS SERVICIOS CONTRATADOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (5, 4, 'SEMOVIENTES');
insert into subfamilias(fami_cod, cod, descripcion)
values (6, 9, 'INSTALACION DE SERVICIOS LOCALES');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 10, 'PRODUCTOS DE USO MARINO');
insert into subfamilias(fami_cod, cod, descripcion, fecha_baja, motivo_baja)
values (2, 11, 'REPUESTOS Y ACCESORIOS', date '2005-07-14', '');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 12, 'REPUESTOS Y ACCESORIOS');
insert into subfamilias(fami_cod, cod, descripcion)
values (2, 13, 'PROD. QUIM. Y CONEXOS-MEDICAMENTOS Y ANTISEPTICOS USO HUMANO');
insert into subfamilias(fami_cod, cod, descripcion)
values (5, 6, 'OTRAS TIERRAS, EDIFICIOS Y OTROS BIENES DE USO');
