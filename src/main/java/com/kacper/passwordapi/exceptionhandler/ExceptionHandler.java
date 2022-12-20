package com.kacper.passwordapi.exceptionhandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(ConstraintViolationException exception){
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
