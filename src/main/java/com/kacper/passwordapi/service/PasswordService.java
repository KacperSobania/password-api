package com.kacper.passwordapi.service;

import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.entity.Password;
import com.kacper.passwordapi.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;

    public List<PasswordDto> createPassword(int length, boolean specialCharactersPresence, boolean lowerCasePresence, boolean capitalCasePresence, int numberOfPasswords){
        List<PasswordDto> passwordDtos = new ArrayList<>();
        for(int i = 0; i < numberOfPasswords; i++){
            String password = generatePassword(length, specialCharactersPresence, lowerCasePresence, capitalCasePresence);
            String complexity = defineComplexity(length, specialCharactersPresence, lowerCasePresence, capitalCasePresence);
            PasswordDto passwordDto = new PasswordDto(password, complexity);
            if(passwordRepository.findFirstByPassword(password) != null){
                passwordDto.setPasswordAlreadyExists(true);
            }
            passwordDtos.add(passwordDto);
            passwordRepository.save(new Password(password, complexity, LocalDateTime.now()));
        }
        return passwordDtos;
    }

    private String generatePassword(int length, boolean specialCharactersPresence, boolean lowerCasePresence, boolean capitalCasePresence){

        List<CharacterRule> rules = new ArrayList<>();
        if(lowerCasePresence){
            rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        }
        if(capitalCasePresence){
            rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        }
        if(specialCharactersPresence){
            rules.add(new CharacterRule(new CharacterData() {
                @Override
                public String getErrorCode() {
                    return "ERR_SPACE";
                }

                @Override
                public String getCharacters() {
                    return "!@#$%^&*()-_=+[]{}|?/<>,.;:'`~";
                }
            }, 1));
        }
        PasswordGenerator generator = new PasswordGenerator();

        return generator.generatePassword(length, rules);
    }

    private String defineComplexity(int length, boolean specialCharactersPresence, boolean lowerCasePresence, boolean capitalCasePresence) {
        if (length > 16 && specialCharactersPresence && lowerCasePresence && capitalCasePresence) {
            return "very strong";
        } else if(length > 8 && specialCharactersPresence && lowerCasePresence && capitalCasePresence) {
            return "strong";
        } else if(length > 5 && !specialCharactersPresence){
            return "medium";
        } else {
            return "weak";
        }
    }

}
