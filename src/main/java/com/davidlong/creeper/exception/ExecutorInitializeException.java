package com.davidlong.creeper.exception;

public class ExecutorInitializeException extends RuntimeException {
    public ExecutorInitializeException() {
    }

    public ExecutorInitializeException(String message) {
        super(message);
    }

    public ExecutorInitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutorInitializeException(Throwable cause) {
        super(cause);
    }
}
