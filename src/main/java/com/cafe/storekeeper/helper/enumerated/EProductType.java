package com.cafe.storekeeper.helper.enumerated;

public enum EProductType {
	ARTICLE("AR"),
	SERVICE("SE");

	private final String nombre;

	private EProductType(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
