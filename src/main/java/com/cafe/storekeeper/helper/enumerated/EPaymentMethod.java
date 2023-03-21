package com.cafe.storekeeper.helper.enumerated;

public enum EPaymentMethod {
	CASH("E"),
	CREDIT_CARD("TD"),
	DEBIT_CARD("TC");

	private final String nombre;

	private EPaymentMethod(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}
}
