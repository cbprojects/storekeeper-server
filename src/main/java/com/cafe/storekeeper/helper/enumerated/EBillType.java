package com.cafe.storekeeper.helper.enumerated;

public enum EBillType {
    SALE_ORDER("SO"),
    PURCHASE_ORDER("PO"),
    INCOME("IN"),
    COST("CO"),
    EXPENSE("EX"),
    ANNULMENT("AN"),
    FIX("FI");

    private final String nombre;

    private EBillType(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
