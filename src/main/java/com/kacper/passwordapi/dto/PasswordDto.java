package com.kacper.passwordapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PasswordDto {
    private final String password;
    private final String complexity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime created;
}
