package com.davidlong.http.exception;

public class RuntimeExecuteException extends RuntimeException {
    public RuntimeExecuteException() {
    }

    public RuntimeExecuteException(String message) {
        super(message);
    }

    public RuntimeExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeExecuteException(Throwable cause) {
        super(cause);
    }
}

