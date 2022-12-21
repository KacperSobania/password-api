package com.kacper.passwordapi.exceptionhandler;

import com.kacper.passwordapi.exception.UnacceptableValuesOfParametersException;
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

    @org.springframework.web.bind.annotation.ExceptionHandler(UnacceptableValuesOfParametersException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<ErrorResponse> unacceptableValuesOfParametersExceptionHandler(UnacceptableValuesOfParametersException exception){
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.PRECONDITION_FAILED, exception.getMessage()), HttpStatus.PRECONDITION_FAILED);
    }

}
