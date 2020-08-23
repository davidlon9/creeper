package com.dlong.creeper.exception;

public class ResolveException extends Exception {
    public ResolveException(String message) {
        super(message);
    }

    public ResolveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolveException(Throwable cause) {
        super(cause);
    }

    public ResolveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ResolveException() {
    }
}
