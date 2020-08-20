package com.davidlong.creeper.exception;

public class RuntimeResolveException extends RuntimeException {
    public RuntimeResolveException(String message) {
        super(message);
    }

    public RuntimeResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeResolveException(Throwable cause) {
        super(cause);
    }

    public RuntimeResolveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RuntimeResolveException() {
    }
}
