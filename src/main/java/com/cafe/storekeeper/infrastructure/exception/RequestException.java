package com.cafe.storekeeper.infrastructure.exception;

import com.cafe.storekeeper.helper.enums.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;

public class RequestException extends Exception {

    private static final String LOG_START = "|~ RequestException ~>| ";
    private static final String LOG_END = " |<~ RequestException ~|";

    public RequestException(String message) {
        super(LOG_START.concat(message).concat(LOG_END));
    }

    public RequestException(StandardErrorResponse error) {
        super(LOG_START.concat(error.toString()).concat(LOG_END));
    }

    public RequestException(EMapApiErrors error) {
        super(LOG_START.concat(new StandardErrorResponse(error).toString()).concat(LOG_END));
    }

}