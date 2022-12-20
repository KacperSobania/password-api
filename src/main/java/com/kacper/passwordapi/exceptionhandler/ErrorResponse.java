package com.kacper.passwordapi.exceptionhandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(HttpStatus httpStatus, String message){
        timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.name();
        this.message = message;
    }
}
