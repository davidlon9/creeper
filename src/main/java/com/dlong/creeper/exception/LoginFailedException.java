package com.dlong.creeper.exception;

public class LoginFailedException extends Exception{
    public LoginFailedException() {
        super();
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
