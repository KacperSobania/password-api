package com.kacper.passwordapi.controller;

import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/password-api")
public class PasswordController {

    private final PasswordService passwordService;

    @GetMapping("/generate-password")
    public List<PasswordDto> createPassword(@RequestParam int length, @RequestParam(defaultValue = "false") boolean specialCharactersPresence, @RequestParam(defaultValue = "false") boolean lowerCaseLettersPresence, @RequestParam(defaultValue = "false") boolean capitalCaseLettersPresence, @RequestParam int numberOfPasswords){
        return passwordService.createPassword(length, specialCharactersPresence, lowerCaseLettersPresence, capitalCaseLettersPresence, numberOfPasswords);
    }
}
