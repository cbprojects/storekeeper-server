package com.cafe.storekeeper.helper.enums;

public enum EBillType {
    AJUSTE("AJ"),
    ANULACION("AN"),
    COMPRA("CO"),
    COSTO("CS"),
    COTIZACION("CT"),
    GASTO("GA"),
    INGRESO("IN"),
    VENTA("VE");

    private final String nombre;

    private EBillType(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
