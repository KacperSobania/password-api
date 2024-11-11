package com.kacper.passwordapi.service;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.entity.Password;
import com.kacper.passwordapi.enums.PasswordComplexity;
import com.kacper.passwordapi.exception.NotFoundException;
import com.kacper.passwordapi.exception.UnacceptableValuesOfParametersException;
import com.kacper.passwordapi.repository.PasswordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordServiceTest {

    private PasswordService passwordService;
    private PasswordRepository passwordRepository = Mockito.mock(PasswordRepository.class);

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(passwordRepository);
    }

    @Nested
    class CreatePassword {
        @Test
        void createPasswordWithUnacceptableParamsValues() {
            Throwable exception = assertThrows(UnacceptableValuesOfParametersException.class, () -> passwordService.createPassword(11, false, false, false, 6));

            assertEquals("At least one parameter value must be true", exception.getMessage());
        }

        @Test
        void createPasswordsThatDoesNotExistInDatabase() {
            when(passwordRepository.findFirstByPassword(any(String.class))).thenReturn(null);

            List<GeneratedPasswordDto> generatedPasswordDtos = passwordService.createPassword(7, false, true, true, 3);

            assertEquals(3, generatedPasswordDtos.size());
            for(int i = 0; i < 3; i++){
                assertEquals(7, generatedPasswordDtos.get(i).getPassword().length());
                assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[a-z].*"));
                assertTrue(generatedPasswordDtos.get(i).getPassword().matches(".*[A-Z].*"));
                assertFalse(generatedPasswordDtos.get(i).getPassword().matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|?/<>,.;:'`~].*"));
                assertEquals(PasswordComplexity.MEDIUM.toString(), generatedPasswordDtos.get(i).getComplexity());
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
                assertEquals(PasswordComplexity.STRONG.toString(), generatedPasswordDtos.get(i).getComplexity());
                assertEquals(true, generatedPasswordDtos.get(i).getPasswordAlreadyExists());
            }
        }
    }

    @Nested
    class VerifyPassword {

        @Nested
        class VeryStrong {
            @Test
            void verifyThatPasswordIsNotVeryStrongWhenSpecialCharactersNotPresent() {
                final String PASSWORD = "ifaDwVfLaPbiKiuEZe";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.VERY_STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotVeryStrongWhenCapitalLettersNotPresent() {
                final String PASSWORD = "i!e!{b~ffl&@av,.w)";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.VERY_STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotVeryStrongWhenLowerLettersNotPresent() {
                final String PASSWORD = "ILL!&AMA^=N&VT.KU+";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.VERY_STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotVeryStrongWhenTooShort() {
                final String PASSWORD = "Ia@w^vnf,ww&W$";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.VERY_STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsVeryStrong() {
                final String PASSWORD = "Q{cRb]l=xo+^v&TOvn";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertEquals(PasswordComplexity.VERY_STRONG.toString(), passwordDto.getComplexity());
            }
        }

        @Nested
        class Strong {
            @Test
            void verifyThatPasswordIsNotStrongWhenSpecialCharactersNotPresent() {
                final String PASSWORD = "paXaCerZnfS";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotStrongWhenCapitalLettersNotPresent() {
                final String PASSWORD = "%el~^wg{f'+";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotStrongWhenLowerLettersNotPresent() {
                final String PASSWORD = "O,%KM]FI&.R";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotStrongWhenTooShort() {
                final String PASSWORD = "@DcgEXo";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.VERY_STRONG.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsStrong() {
                final String PASSWORD = "q%}V(Y-$Ei@";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertEquals(PasswordComplexity.STRONG.toString(), passwordDto.getComplexity());
            }
        }

        @Nested
        class Medium {
            @Test
            void verifyThatPasswordIsNotMediumWhenTooShort() {
                final String PASSWORD = "eDNRj";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.MEDIUM.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsNotMediumWhenOnlyLowerLettersPresent() {
                final String PASSWORD = "djvkcdl";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertNotEquals(PasswordComplexity.MEDIUM.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsMediumWhenLongAndCapitalAndLowerLettersPresent() {
                final String PASSWORD = "AywiEdqjyWpLvNmYiVJ";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertEquals(PasswordComplexity.MEDIUM.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsMediumWhenLongAndSpecialCharactersAndLowerLettersPresent() {
                final String PASSWORD = "]djt(xoa{q_xzs!el=)";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertEquals(PasswordComplexity.MEDIUM.toString(), passwordDto.getComplexity());
            }

            @Test
            void verifyThatPasswordIsMediumWhenLongAndSpecialCharactersAndCapitalLettersPresent() {
                final String PASSWORD = "!J^N%IU~OGBLVK_^M";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertEquals(PasswordComplexity.MEDIUM.toString(), passwordDto.getComplexity());
            }
        }

        @Nested
        class Weak {
            @Test
            void verifyThatPasswordIsWeakWhenShort() {
                final String PASSWORD = "oUQl";

                when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

                PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

                assertEquals(PASSWORD, passwordDto.getPassword());
                assertEquals(PasswordComplexity.WEAK.toString(), passwordDto.getComplexity());
            }
        }

        @Test
        void verifyPasswordThatAlreadyExistsInDatabase() {
            final String PASSWORD = "aXZ@fj*m=";
            final String PASSWORD_COMPLEXITY = PasswordComplexity.STRONG.toString();

            Password password = new Password(PASSWORD, PASSWORD_COMPLEXITY, LocalDateTime.now());

            when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(password);

            PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

            assertEquals(PASSWORD, passwordDto.getPassword());
            assertEquals(PASSWORD_COMPLEXITY, passwordDto.getComplexity());
        }

        @Test
        void verifyPasswordThatDoesNotExistsInDatabase() {
            final String PASSWORD = "op@x";
            final String PASSWORD_COMPLEXITY = PasswordComplexity.WEAK.toString();

            when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

            PasswordDto passwordDto = passwordService.verifyPassword(PASSWORD);

            assertEquals(PASSWORD, passwordDto.getPassword());
            assertEquals(PASSWORD_COMPLEXITY, passwordDto.getComplexity());
            assertNull(passwordDto.getCreated());
        }
    }

    @Nested
    class RemovePassword {
        @Test
        void removePasswordThatAlreadyExistsInDatabase() {
            final String PASSWORD = "RMBqIpf";
            final String PASSWORD_COMPLEXITY = PasswordComplexity.MEDIUM.toString();

            Password password = new Password(PASSWORD, PASSWORD_COMPLEXITY, LocalDateTime.now());

            when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(password);

            PasswordDto passwordDto = passwordService.removePassword(PASSWORD);

            verify(passwordRepository).deleteByPassword(PASSWORD);
            assertEquals(PASSWORD, passwordDto.getPassword());
            assertEquals(PASSWORD_COMPLEXITY, passwordDto.getComplexity());
        }

        @Test
        void removePasswordThatDoesNotExistsInDatabase() {
            final String PASSWORD = "#%Ou&s*x>/:poZQW^+a";

            when(passwordRepository.findFirstByPassword(PASSWORD)).thenReturn(null);

            Throwable exception = assertThrows(NotFoundException.class, () -> passwordService.removePassword(PASSWORD));

            assertEquals("Password not found", exception.getMessage());
        }
    }
}