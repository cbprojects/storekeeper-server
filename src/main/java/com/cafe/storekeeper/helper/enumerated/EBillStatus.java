package com.cafe.storekeeper.helper.enumerated;

public enum EBillStatus {
    PENDING("PE"),
    PAID("PA"),
    CANCELED("CA"),
    ANULLED("AN"),
    ERROR("ER"),
    EXPIRED("EX");

    private final String nombre;

    private EBillStatus(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
