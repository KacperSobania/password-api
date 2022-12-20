package com.kacper.passwordapi.service;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.entity.Password;
import com.kacper.passwordapi.repository.PasswordRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public List<GeneratedPasswordDto> createPassword(int length, boolean specialCharactersPresence, boolean lowerCasePresence, boolean capitalCasePresence, int numberOfPasswords){
        List<GeneratedPasswordDto> generatedPasswordDtos = new ArrayList<>();
        for(int i = 0; i < numberOfPasswords; i++){
            String password = generatePassword(length, specialCharactersPresence, lowerCasePresence, capitalCasePresence);
            String complexity = definePasswordComplexity(password);
            GeneratedPasswordDto generatedPasswordDto = new GeneratedPasswordDto(password, complexity);
            if(passwordRepository.findFirstByPassword(password) != null){
                generatedPasswordDto.setPasswordAlreadyExists(true);
            }
            generatedPasswordDtos.add(generatedPasswordDto);
            passwordRepository.save(new Password(password, complexity, LocalDateTime.now()));
        }
        return generatedPasswordDtos;
    }

    public PasswordDto verifyPassword(String password){
        Password passwordFromDatabase = passwordRepository.findFirstByPassword(password);
        if(passwordFromDatabase != null){
            return new PasswordDto(passwordFromDatabase.getPassword(), passwordFromDatabase.getComplexity(), passwordFromDatabase.getCreated());
        } else{
            String complexity = definePasswordComplexity(password);
            return new PasswordDto(password, complexity, null);
        }
    }

    @Transactional
    public PasswordDto removePassword(String password){
        Password passwordFromDatabase = passwordRepository.findFirstByPassword(password);
        if(passwordFromDatabase != null){
            PasswordDto passwordDto = new PasswordDto(passwordFromDatabase.getPassword(), passwordFromDatabase.getComplexity(), null);
            passwordRepository.deleteByPassword(password);
            return passwordDto;
        } else{
            String complexity = definePasswordComplexity(password);
            return new PasswordDto(password, complexity, null);
        }
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

    private String definePasswordComplexity(String password) {
        int passwordLength = password.length();
        boolean specialCharactersPresence = password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|?/<>,.;:'`~].*");
        boolean lowerCasePresence = password.matches(".*[a-z].*");
        boolean capitalCasePresence = password.matches(".*[A-Z].*");
        if (passwordLength > 16 && specialCharactersPresence && lowerCasePresence && capitalCasePresence) {
            return "very strong";
        } else if(passwordLength > 8 && specialCharactersPresence && lowerCasePresence && capitalCasePresence) {
            return "strong";
        } else if(passwordLength > 5 && !specialCharactersPresence){
            return "medium";
        } else {
            return "weak";
        }
    }

}