package com.kacper.passwordapi.controller;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/password-api")
public class PasswordController {

    private final PasswordService passwordService;

    @GetMapping("/passwords")
    public List<GeneratedPasswordDto> createPassword(@RequestParam(name = "lgth") int length, @RequestParam(defaultValue = "false", name = "spclCh") boolean specialCharactersPresence, @RequestParam(defaultValue = "true", name = "lwrCsLet") boolean lowerCaseLetterPresence, @RequestParam(defaultValue = "false", name = "cptCsLet") boolean capitalCaseLetter, @RequestParam(name = "passwords") int numbeOfPasswords){
        return passwordService.createPassword(length, specialCharactersPresence, lowerCaseLetterPresence, capitalCaseLetter, numbeOfPasswords);
    }

    @GetMapping("/verification/{password}")
    public PasswordDto verifyPassword(@PathVariable String password){
        return passwordService.verifyPassword(password);
    }

    @DeleteMapping("/removal/{password}")
    public PasswordDto removePassword(@PathVariable String password){
        return passwordService.removePassword(password);
    }
}
