package com.kacper.passwordapi.controller;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.service.PasswordService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/password-api")
public class PasswordController {

    private final PasswordService passwordService;

    @GetMapping("/passwords")
    public List<GeneratedPasswordDto> createPassword(@RequestParam(name = "lgth") @Min(value = 3, message = "Password must be at least {value} characters long") @Max(value = 32, message = "Password can not be longer than {value} characters") int length, @RequestParam(defaultValue = "false", name = "spclCh") boolean specialCharactersPresence, @RequestParam(defaultValue = "true", name = "lwrCsLet") boolean lowerCaseLetterPresence, @RequestParam(defaultValue = "false", name = "cptCsLet") boolean capitalCaseLetter, @RequestParam(name = "passwords") @Min(value = 1, message = "Number of generated passwords must be at least {value}") @Max(value = 1000, message = "Number of generated passwords can not be higher than {value}") int numberOfPasswords){
        return passwordService.createPassword(length, specialCharactersPresence, lowerCaseLetterPresence, capitalCaseLetter, numberOfPasswords);
    }

    @GetMapping("/verification/{password}")
    public PasswordDto verifyPassword(@PathVariable @Size(min = 3, max = 32, message = "Password length must be between {min} and {max}") String password){
        return passwordService.verifyPassword(password);
    }

    @DeleteMapping("/removal/{password}")
    public PasswordDto removePassword(@PathVariable @Size(min = 3, max = 32, message = "Password length must be between {min} and {max}") String password){
        return passwordService.removePassword(password);
    }
}
