package com.davidlong.creeper.exception;

public class UnsupportedFluentReturnTypeException extends RuntimeException {
    public UnsupportedFluentReturnTypeException() {
    }

    public UnsupportedFluentReturnTypeException(String message) {
        super(message);
    }

    public UnsupportedFluentReturnTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedFluentReturnTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedFluentReturnTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
