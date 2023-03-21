package com.cafe.storekeeper.helper.enumerated;

import java.util.HashMap;
import java.util.Map;

import com.cafe.storekeeper.helper.constant.ConstantsMessage;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;

public enum EMapApiErrors {

    // GENERIC SERVER
    ERROR_SERVER("ERROR_SERVER",
            ConstantsMessage.ERROR_DEFAULT,
            ConstantsMessage.ERROR_INTERNAL_SERVER_DEFAULT),

    // GENERIC PARAMS
    ERROR_QUERY_PARAMS("ERROR_QUERY_PARAMS",
            ConstantsMessage.ERROR_DEFAULT,
            ConstantsMessage.PARAMETER_NOT_PROVIDED),

    // OTHER ERRORS
    ERROR_DATA_NOT_FOUND("ERROR_DATA_NOT_FOUND",
            ConstantsMessage.ERROR_DEFAULT,
            ConstantsMessage.DATA_NOT_FOUND);

    private final String code;
    private final String detail;
    private final String message;

    private EMapApiErrors(String code, String detail, String message) {
        this.code = code;
        this.detail = detail;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public String getMessage() {
        return message;
    }

    public static Map<String, StandardErrorResponse> getMapErrors() {
        Map<String, StandardErrorResponse> result = new HashMap<>();
        for (EMapApiErrors enumValue : values()) {
            StandardErrorResponse errorDTO = new StandardErrorResponse(enumValue);
            if (result.isEmpty() || (result.get(errorDTO.getCode()) == null)) {
                result.put(errorDTO.getCode(), errorDTO);
            }
        }
        return result;
    }

    public static StandardErrorResponse findByCode(String errorCodeAPI) {
        Map<String, StandardErrorResponse> mapErrors = getMapErrors();
        StandardErrorResponse error = mapErrors.get(errorCodeAPI);
        if (error == null) {
            error = new StandardErrorResponse(ERROR_SERVER);
        }
        return error;
    }

}