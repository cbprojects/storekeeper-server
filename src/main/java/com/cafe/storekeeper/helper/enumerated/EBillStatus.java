package com.cafe.storekeeper.helper.enumerated;

public enum EBillStatus {
    ANULADA("AN"),
    CANCELADA("CA"),
    ERROR("ER"),
    PAGADA("PA"),
    PENDIENTE("PE"),
    VENCIDA("VE");

    private final String nombre;

    private EBillStatus(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
