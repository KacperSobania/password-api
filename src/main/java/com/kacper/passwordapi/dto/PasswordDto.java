package com.kacper.passwordapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class PasswordDto {
    private final String password;
    private final String complexity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter
    private Boolean passwordAlreadyExists;
}
