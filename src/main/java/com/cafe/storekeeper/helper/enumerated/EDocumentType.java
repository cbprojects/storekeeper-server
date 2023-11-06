package com.cafe.storekeeper.helper.enumerated;

public enum EDocumentType {
	CEDULA("CC"),
	CEDULA_EXTRANJERIA("CE"),
	NIT("NI"),
	PASAPORTE("PA");

	private final String nombre;

	private EDocumentType(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
