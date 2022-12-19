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
    public List<PasswordDto> createPassword(@RequestParam(name = "lgth") int length, @RequestParam(defaultValue = "false", name = "spclCh") boolean specialCharactersPresence, @RequestParam(defaultValue = "true", name = "lwrCsLet") boolean lowerCaseLetterPresence, @RequestParam(defaultValue = "false", name = "cptCsLet") boolean capitalCaseLetter, @RequestParam(name = "passwords") int numbeOfPasswords){
        return passwordService.createPassword(length, specialCharactersPresence, lowerCaseLetterPresence, capitalCaseLetter, numbeOfPasswords);
    }
}
