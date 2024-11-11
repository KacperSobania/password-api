package com.kacper.passwordapi.controller;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.entity.Password;
import com.kacper.passwordapi.enums.PasswordComplexity;
import com.kacper.passwordapi.exceptionhandler.ErrorResponse;
import com.kacper.passwordapi.repository.PasswordRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PasswordControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PasswordRepository passwordRepository;

    @Nested
    class createPassword {
        @Test
        void createPasswordWithInvalidPasswordsNumberShouldReturnConstraintViolationException() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .get(createServerAddress(
                            "/passwords?lgth=9&spclCh=true&lwrCsLet=true&cptCsLet=true&passwords=1001"
                    )).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(422, response.getBody().getStatus());
            assertEquals("UNPROCESSABLE_ENTITY", response.getBody().getError());
            assertEquals("createPassword.numberOfPasswords: Number of generated passwords can not be higher than 1000", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        void createPasswordWithNegativePasswordsNumberShouldReturnConstraintViolationException() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .get(createServerAddress(
                            "/passwords?lgth=4&spclCh=false&lwrCsLet=true&cptCsLet=false&passwords=-1"
                    )).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(422, response.getBody().getStatus());
            assertEquals("UNPROCESSABLE_ENTITY", response.getBody().getError());
            assertEquals("createPassword.numberOfPasswords: Number of generated passwords must be at least 1", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        void createPasswordShouldReturnUnacceptableValuesOfParametersException() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .get(createServerAddress(
                            "/passwords?lgth=9&spclCh=false&lwrCsLet=false&cptCsLet=false&passwords=5"
                    )).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(412, response.getBody().getStatus());
            assertEquals("PRECONDITION_FAILED", response.getBody().getError());
            assertEquals("At least one parameter value must be true", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        void createPasswordInvalidLengthShouldReturnConstraintViolationException() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .get(createServerAddress(
                            "/passwords?lgth=2&spclCh=true&lwrCsLet=false&cptCsLet=true&passwords=3"
                    )).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(422, response.getBody().getStatus());
            assertEquals("UNPROCESSABLE_ENTITY", response.getBody().getError());
            assertEquals("createPassword.length: Password must be at least 3 characters long", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        void createPasswordShouldReturnListOfPasswords() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .get(createServerAddress(
                            "/passwords?lgth=11&spclCh=true&lwrCsLet=true&cptCsLet=true&passwords=4"
                    )).build();

            ResponseEntity<List<GeneratedPasswordDto>> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<List<GeneratedPasswordDto>>() {});

            List<Password> passwords = passwordRepository.findAll();

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(4, response.getBody().size());
            assertEquals(passwords.size(), 4);

            for(int i = 0; i < 4; i++){
                assertEquals(PasswordComplexity.STRONG.toString(), response.getBody().get(i).getComplexity());
            }
        }
    }

    @Nested
    class VerifyPassword {
        @Test
        void verifyPasswordWithInvalidLengthShouldReturnConstraintViolationException() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .get(createServerAddress(
                            "/verification/lv"
                    )).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(422, response.getBody().getStatus());
            assertEquals("UNPROCESSABLE_ENTITY", response.getBody().getError());
            assertEquals("verifyPassword.password: Password length must be between 3 and 32", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        void verifyPasswordThatAlreadyExistInDatabase() throws Exception {
            final String PASSWORD = "@fH%$olGVzq";
            passwordRepository.save(new Password(PASSWORD, "STRONG", null));
            RequestEntity<Void> request = RequestEntity.get(createServerAddress("/verification/%40fH%25%24olGVzq")).build();

            ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(PASSWORD, response.getBody().getPassword());
            assertEquals(PasswordComplexity.STRONG.toString(), response.getBody().getComplexity());
        }

        @Test
        void verifyPasswordThatDoesNotExistInDatabase() throws Exception {
            final String PASSWORD = "oPqfGXX";
            passwordRepository.save(new Password(PASSWORD, "MEDIUM", null));

            RequestEntity<Void> request = RequestEntity.get(createServerAddress("/verification/oPqfGXX")).build();

            ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(PASSWORD, response.getBody().getPassword());
            assertEquals(PasswordComplexity.MEDIUM.toString(), response.getBody().getComplexity());
            assertNull(response.getBody().getCreated());
        }
    }

    @Nested
    class RemovePassword {
        @Test
        void removePasswordWithInvalidLengthShouldReturnConstraintViolationException() throws Exception {
            RequestEntity<Void> request = RequestEntity
                    .delete(createServerAddress(
                            "/removal/aabbccddeeffgghhiijjkkllmmnnooppqqrrsstt"
                    )).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(422, response.getBody().getStatus());
            assertEquals("UNPROCESSABLE_ENTITY", response.getBody().getError());
            assertEquals("removePassword.password: Password length must be between 3 and 32", response.getBody().getMessage());
            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        void removePasswordThatExistsInDatabase() throws Exception{
            final String PASSWORD = "!_ySL&?vE~e|wwDHk{a";
            passwordRepository.save(new Password(PASSWORD, "VERY_STRONG", null));
            RequestEntity<Void> request = RequestEntity.delete(createServerAddress("/removal/%21_ySL%26%3FvE~e%7CwwDHk%7Ba")).build();

            ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

            List<Password> passwords = passwordRepository.findAll();

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(PASSWORD, response.getBody().getPassword());
            assertEquals(PasswordComplexity.VERY_STRONG.toString(), response.getBody().getComplexity());
            assertEquals(passwords.size(), 0);
            assertNull(response.getBody().getCreated());
        }

        @Test
        void removePasswordThatDoesNotExistsInDatabase() throws Exception{
            RequestEntity<Void> request = RequestEntity.delete(createServerAddress("/removal/HQBZ")).build();

            ResponseEntity<ErrorResponse> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<ErrorResponse>() {});

            assertTrue(response.getStatusCode().is4xxClientError());

            assertEquals(404, response.getBody().getStatus());
            assertEquals("NOT_FOUND", response.getBody().getError());
            assertEquals("Password not found", response.getBody().getMessage());
        }
    }

    private URI createServerAddress(String endpoint) throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/password-api" + endpoint);
    }
}