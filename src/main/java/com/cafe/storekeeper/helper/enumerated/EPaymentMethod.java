package com.cafe.storekeeper.helper.enumerated;

public enum EPaymentMethod {
	EFECTIVO("EF"),
	TARJETA_DEBITO("TD"),
	TARJETA_CREDITO("TC");

	private final String nombre;

	private EPaymentMethod(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
