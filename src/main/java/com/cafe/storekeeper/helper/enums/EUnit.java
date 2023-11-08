package com.cafe.storekeeper.helper.enums;

public enum EUnit {
	BULTO("BU"),
	CANECA("CA"),
	FRASCO("FR"),
	GALON("GA"),
	GRAMO("GR"),
	HORA("HR"),
	LITRO("LI"),
	METRO("ME"),
	METRO_CUADRADO("M2"),
	UNIDAD("UN");

	private final String nombre;

	private EUnit(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
