package com.kacper.passwordapi.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(){
            super("Password not found");
        }
}
