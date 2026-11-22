package com.cafe.storekeeper.platform.identity;

import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DuplicateKeyException;

@Order(-10)
@RestControllerAdvice(basePackages = "com.cafe.storekeeper.platform")
public class PlatformErrors {
    @ExceptionHandler(IdentityProblem.class)
    public ResponseEntity<ProblemDetail> identity(IdentityProblem error) {
        return ResponseEntity.status(error.status()).body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(error.status()), error.getMessage()));
    }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class,
        org.springframework.http.converter.HttpMessageNotReadableException.class,
        org.springframework.web.bind.MissingRequestHeaderException.class,
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ProblemDetail> invalid(Exception error) {
        return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Revisa los datos proporcionados"));
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ProblemDetail> conflict(DuplicateKeyException error) {
        return ResponseEntity.status(409).body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "La operación ya existe"));
    }
    @ExceptionHandler({org.springframework.dao.TransientDataAccessException.class, org.springframework.transaction.TransactionException.class})
    public ResponseEntity<ProblemDetail> retry(Exception error) {
        return ResponseEntity.status(409).body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "La operación no pudo confirmarse; recarga y reintenta con la misma clave"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> unexpected(Exception error) {
        Throwable cause=error;
        while(cause!=null) {
            if(cause instanceof com.mongodb.MongoException mongo &&
                    (mongo.hasErrorLabel(com.mongodb.MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL)
                    || mongo.hasErrorLabel(com.mongodb.MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL)))
                return retry(error);
            cause=cause.getCause();
        }
        return ResponseEntity.internalServerError().body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "No fue posible completar la operación"));
    }
}
