package com.cafe.storekeeper.helper.constant;

public class ConstantsMessage {

    private ConstantsMessage() {
    }

    public static final String ERROR_INTERNAL_SERVER_DEFAULT = "Internal server error.";
    public static final String ERROR_DEFAULT = "An error has occurred, please try again.";
    public static final String PARAMETER_NOT_PROVIDED = "Parameter not provided.";
    public static final String DATA_NOT_FOUND = "Data not found";

    // Assignment
    public static final String ASSIGNMENT_NO_EMPLOYEE_MESSAGE = "Seleccione un empleado para la asignación.";
    public static final String ASSIGNMENT_EMPTY_CLIENTS_MESSAGE = "La asignación debe realizarse para al menos un cliente.";

    // Bill
    public static final String BILL_NO_DATE_MESSAGE = "Debe seleccionar una fecha válida para la factura.";
    public static final String BILL_NO_EXPIRY_DATE_MESSAGE = "Debe seleccionar una fecha válida de vencimiento para la factura.";
    public static final String BILL_NO_PAYMENT_METHOD_MESSAGE = "Seleccione un método de pago.";
    public static final String BILL_NO_TYPE_MESSAGE = "Debe seleccionar el tipo de facturación.";
    public static final String BILL_EMPTY_CONCEPTS_MESSAGE = "Debe agregar al menos un item/concepto para facturar.";
    public static final String BILL_NO_ADDRESS_MESSAGE = "Debe diligenciar una dirección para facturación.";
    public static final String BILL_NO_CITY_MESSAGE = "La factura no tiene información de la ciudad de emisión.";
    public static final String BILL_NO_STATUS_MESSAGE = "Seleccione el estado de la factura.";
    public static final String BILL_NO_CLIENT_MESSAGE = "Seleccione el cliente para facturar.";

    // Concept Bill
    public static final String CONCEPT_BILL_NO_PROVIDER_MESSAGE = "Todos los items/conceptos de la factura deben tener la información de su proveedor.";

    // Product
    public static final String PRODUCT_NO_PROVIDER_MESSAGE = "Seleccione el proveedor para facturar.";

}