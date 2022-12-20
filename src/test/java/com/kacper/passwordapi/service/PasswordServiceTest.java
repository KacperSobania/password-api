package com.kacper.passwordapi.service;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.entity.Password;
import com.kacper.passwordapi.repository.PasswordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PasswordServiceTest {

    private PasswordService passwordService;
    private PasswordRepository passwordRepository = Mockito.mock(PasswordRepository.class);

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(passwordRepository);
    }

    @Test
    void createPasswordThatDoNotExistInDatabase() {
        when(passwordRepository.findFirstByPassword(any(String.class))).thenReturn(null);

        List<GeneratedPasswordDto> generatedPasswordDtos = passwordService.createPassword(7, false, true, true, 3);

        assertEquals(3, generatedPasswordDtos.size());
        for(int i = 0; i < 3; i++){
            assertEquals(7, generatedPasswordDtos.get(i).getPassword().length());
            assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[a-z].*"));
            assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[A-Z].*"));
            assertFalse(generatedPasswordDtos.get(i).getPassword().matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|?/<>,.;:'`~].*"));
            assertEquals("medium", generatedPasswordDtos.get(i).getComplexity());
            assertNull(generatedPasswordDtos.get(i).getPasswordAlreadyExists());
        }
    }

    @Test
    void createPasswordThatAlreadyExistInDatabase() {
        when(passwordRepository.findFirstByPassword(any(String.class))).thenReturn(new Password("yT$;CC.bfX_", "strong", LocalDateTime.now()));

        List<GeneratedPasswordDto> generatedPasswordDtos = passwordService.createPassword(11, true, true, true, 2);
        assertEquals(2, generatedPasswordDtos.size());
        for(int i = 0; i < 2; i++){
            assertEquals(11, generatedPasswordDtos.get(i).getPassword().length());
            assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[a-z].*"));
            assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[A-Z].*"));
            assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|?/<>,.;:'`~].*"));
            assertEquals("strong", generatedPasswordDtos.get(i).getComplexity());
            assertNotNull(generatedPasswordDtos.get(i).getPasswordAlreadyExists());
        }
    }

    @Test
    void verifyPasswordThatAlreadyExistsInDatabase() {
        Password password = new Password("aXZ@fj*m=", "strong", LocalDateTime.now());

        when(passwordRepository.findFirstByPassword("aXZ@fj*m=")).thenReturn(password);

        PasswordDto passwordDto = passwordService.verifyPassword("aXZ@fj*m=");

        assertEquals("aXZ@fj*m=", passwordDto.getPassword());
        assertEquals("strong", passwordDto.getComplexity());
    }

    @Test
    void verifyPasswordThatDoNotExistsInDatabase() {
        when(passwordRepository.findFirstByPassword("op@x")).thenReturn(null);

        PasswordDto passwordDto = passwordService.verifyPassword("op@x");

        assertEquals("op@x", passwordDto.getPassword());
        assertEquals("weak", passwordDto.getComplexity());
        assertNull(passwordDto.getCreated());
    }

    @Test
    void removePasswordThatAlreadyExistsInDatabase() {
        Password password = new Password("RMBqIpf", "medium", LocalDateTime.now());

        when(passwordRepository.findFirstByPassword("RMBqIpf")).thenReturn(password);

        PasswordDto passwordDto = passwordService.removePassword("RMBqIpf");

        assertEquals("RMBqIpf", passwordDto.getPassword());
        assertEquals("medium", passwordDto.getComplexity());
    }

    @Test
    void removePasswordThatDoNotExistsInDatabase() {
        when(passwordRepository.findFirstByPassword("#%Ou&s*x>/:poZQW^+a")).thenReturn(null);

        PasswordDto passwordDto = passwordService.removePassword("#%Ou&s*x>/:poZQW^+a");

        assertEquals("#%Ou&s*x>/:poZQW^+a", passwordDto.getPassword());
        assertEquals("very strong", passwordDto.getComplexity());
        assertNull(passwordDto.getCreated());
    }
}