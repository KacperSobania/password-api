package com.kacper.passwordapi.exception;

public class UnacceptableValuesOfParametersException extends RuntimeException{

    public UnacceptableValuesOfParametersException(){
        super("At least one parameter value must be true");
    }
}
