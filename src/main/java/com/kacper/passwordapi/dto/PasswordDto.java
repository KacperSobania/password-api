package com.kacper.passwordapi.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordDto {
    private final String password;
    private final String complexity;
}
