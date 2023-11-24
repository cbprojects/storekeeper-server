package com.cafe.storekeeper.infrastructure.exception;

import javax.validation.UnexpectedTypeException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cafe.storekeeper.helper.constant.ConstantsValidators;
import com.cafe.storekeeper.helper.util.UtilResponse;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RestController
@Slf4j
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String BD_NOT_FOUND = "No value present";

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<StandardErrorResponse> handleExceptions(Exception exception) {
        log.error("****** [storekeeper] ****** Exception {}", exception.getMessage());
        StandardErrorResponse response = UtilResponse.buildApiError(exception,
                ConstantsValidators.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public final ResponseEntity<StandardErrorResponse> handleUnexpectedTypeException(
            UnexpectedTypeException exception) {
        log.error("****** [storekeeper] ****** UnexpectedTypeException {}", exception.getMessage());
        StandardErrorResponse response = UtilResponse.buildApiError(exception, ConstantsValidators.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ModelException.class)
    public final ResponseEntity<StandardErrorResponse> handleBDExceptions(ModelException exception,
            WebRequest request) {
        log.error("****** [storekeeper] ******  ModelException {}", exception.getMessage());
        if (BD_NOT_FOUND.equalsIgnoreCase(exception.getMessage())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        StandardErrorResponse apiErrorDTO = UtilResponse.buildApiError(exception, ConstantsValidators.BD_ERROR);
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestException.class)
    public final ResponseEntity<StandardErrorResponse> handleRequestExceptions(RequestException exception) {
        log.error("****** [storekeeper] ******  RequestException {}", exception.getMessage());
        StandardErrorResponse response = UtilResponse.buildApiError(exception, ConstantsValidators.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiClientException.class)
    public final ResponseEntity<StandardErrorResponse> handleApiClientExceptions(ApiClientException exception) {
        log.error("****** [storekeeper] ******  ApiClientException {}", exception.getMessage());
        StandardErrorResponse apiError = UtilResponse.buildApiError(exception, ConstantsValidators.BAD_REQUEST);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}