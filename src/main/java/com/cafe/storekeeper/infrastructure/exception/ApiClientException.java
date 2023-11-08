package com.cafe.storekeeper.infrastructure.exception;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;

public class ApiClientException extends Exception {

    private static final String LOG_START = "|~ ApiClientException ~>| ";
    private static final String LOG_END = " |<~ ApiClientException ~|";

    public ApiClientException(String message) {
        super(LOG_START.concat(message).concat(LOG_END));
    }

    public ApiClientException(StandardErrorResponse error) {
        super(LOG_START.concat(error.toString()).concat(LOG_END));
    }

    public ApiClientException(EMapApiErrors error) {
        super(LOG_START.concat(new StandardErrorResponse(error).toString()).concat(LOG_END));
    }

}