package com.example.reto_backend_febrero2026.familia;

public enum FamiliaModel {
	BIENES_TECNOLOGIAS_INFORMACION_COMUNICACION(10, "BIENES DE TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION"),
	PRODUCTOS_EXCLUIDOS_CATALOGO(12, "PRODUCTOS EXCLUIDOS DEL CATALOGO UNICO DE BIENES Y SERVICIOS"),
	OTROS_GASTOS(11, "OTROS GASTOS"),
	MATERIALES_SUMINISTROS(2, "MATERIALES Y SUMINISTROS"),
	SERVICIOS_NO_PERSONALES(3, "SERVICIOS NO PERSONALES"),
	MAQUINAS_EQUIPOS_MOBILIARIOS(4, "MAQUINAS, EQUIPOS Y MOBILIARIOS NUEVOS"),
	TIERRAS_EDIFICIOS_BIENES_USO(5, "TIERRAS, EDIFICIOS Y OTROS BIENES DE USO"),
	CONSTRUCCIONES_MEJORAS_REPARACIONES(6, "CONSTRUCCIONES, MEJORAS Y REPARACIONES EXTRAORDINARIAS"),
	SERVICIOS_PERSONALES(1, "SERVICIOS PERSONALES");

	private final int cod;
	private final String description;

	FamiliaModel(int cod, String description) {
		this.cod = cod;
		this.description = description;
	}

	public int getCod() {
		return cod;
	}

	public String getDescription() {
		return description;
	}

	public static FamiliaModel findByCod(int cod) {
		for (FamiliaModel familia : values()) {
			if (familia.cod == cod) {
				return familia;
			}
		}
		throw new IllegalArgumentException("No Familia found with cod: " + cod);
	}
}
