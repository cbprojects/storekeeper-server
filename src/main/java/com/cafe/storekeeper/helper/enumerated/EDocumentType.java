package com.cafe.storekeeper.helper.enumerated;

public enum EDocumentType {
	CEDULA("CC"),
	PASAPORTE("PA"),
	CEDULA_EXTRANJERIA("CE"),
	NIT("NI");

	private final String nombre;

	private EDocumentType(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
