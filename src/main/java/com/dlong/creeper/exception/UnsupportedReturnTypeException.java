package com.dlong.creeper.exception;

public class UnsupportedReturnTypeException extends RuntimeException {
    public UnsupportedReturnTypeException() {
        super("返回类型不支持");
    }

    public UnsupportedReturnTypeException(String message) {
        super(message);
    }

    public UnsupportedReturnTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedReturnTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedReturnTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}