package com.banking.cbs.account.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CbsException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public CbsException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static CbsException notFound(String errorCode, String message) {
        return new CbsException(HttpStatus.NOT_FOUND, errorCode, message);
    }

    public static CbsException conflict(String errorCode, String message) {
        return new CbsException(HttpStatus.CONFLICT, errorCode, message);
    }

    public static CbsException badRequest(String errorCode, String message) {
        return new CbsException(HttpStatus.BAD_REQUEST, errorCode, message);
    }

    public static CbsException unprocessable(String errorCode, String message) {
        return new CbsException(HttpStatus.UNPROCESSABLE_ENTITY, errorCode, message);
    }

    public static CbsException serviceUnavailable(String errorCode, String message) {
        return new CbsException(HttpStatus.SERVICE_UNAVAILABLE, errorCode, message);
    }
}
