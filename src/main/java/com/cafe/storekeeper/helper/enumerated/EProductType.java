package com.cafe.storekeeper.helper.enumerated;

public enum EProductType {
	ARTICLE("E"),
	SERVICE("TD");

	private final String nombre;

	private EProductType(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
