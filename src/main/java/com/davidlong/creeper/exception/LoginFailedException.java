package com.davidlong.creeper.exception;

public class LoginFailedException extends Exception{
    public LoginFailedException() {
        super();
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
