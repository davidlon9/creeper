package com.dlong.creeper.exception;

public class MethodNotSupportException extends RuntimeException {
    public MethodNotSupportException() {
        super("entityTarget not support");
    }

    public MethodNotSupportException(String message) {
        super(message);
    }

    public MethodNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNotSupportException(Throwable cause) {
        super(cause);
    }
}

