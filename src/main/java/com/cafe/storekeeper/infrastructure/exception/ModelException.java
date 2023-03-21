package com.cafe.storekeeper.infrastructure.exception;

import com.cafe.storekeeper.helper.enumerated.EMapApiErrors;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;

public class ModelException extends Exception {

    private static final String LOG_START = "|~ ModelException ~>| ";
    private static final String LOG_END = " |<~ ModelException ~|";

    public ModelException(String message) {
        super(LOG_START.concat(message).concat(LOG_END));
    }

    public ModelException(StandardErrorResponse error) {
        super(LOG_START.concat(error.toString()).concat(LOG_END));
    }

    public ModelException(EMapApiErrors error) {
        super(LOG_START.concat(new StandardErrorResponse(error).toString()).concat(LOG_END));
    }

}