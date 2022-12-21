package com.kacper.passwordapi.controller;

import com.kacper.passwordapi.dto.GeneratedPasswordDto;
import com.kacper.passwordapi.dto.PasswordDto;
import com.kacper.passwordapi.exceptionhandler.ErrorResponse;
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
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/insert_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PasswordControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void createPasswordShouldReturnListOfPasswords() throws Exception {
        RequestEntity<Void> request = RequestEntity
                .get(createServerAddress(
                        "/passwords?lgth=9&spclCh=true&lwrCsLet=true&cptCsLet=true&passwords=5"
                )).build();

        ResponseEntity<List<GeneratedPasswordDto>> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<List<GeneratedPasswordDto>>() {});

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(5, response.getBody().size());

        for(int i = 0; i < 5; i++){
            assertEquals("strong", response.getBody().get(i).getComplexity());
        }
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
    void verifyPasswordThatAlreadyExistInDatabase() throws Exception {
        RequestEntity<Void> request = RequestEntity.get(createServerAddress("/verification/%40fH%25%24olGVz")).build();

        ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("@fH%$olGVz", response.getBody().getPassword());
        assertEquals("strong", response.getBody().getComplexity());
        assertNotNull(response.getBody().getCreated());
    }

    @Test
    void verifyPasswordThatDoesNotExistInDatabase() throws Exception {
        RequestEntity<Void> request = RequestEntity.get(createServerAddress("/verification/oPqfGXX")).build();

        ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("oPqfGXX", response.getBody().getPassword());
        assertEquals("medium", response.getBody().getComplexity());
    }

    @Test
    void removePasswordThatExistsInDatabase() throws Exception{
        RequestEntity<Void> request = RequestEntity.delete(createServerAddress("/removal/%21_ySL%26%3FvE~e%7CwwDHk%7B")).build();

        ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("!_ySL&?vE~e|wwDHk{", response.getBody().getPassword());
        assertEquals("very strong", response.getBody().getComplexity());
        assertNull(response.getBody().getCreated());
    }

    @Test
    void removePasswordThatDoesExistsInDatabase() throws Exception{
        RequestEntity<Void> request = RequestEntity.delete(createServerAddress("/removal/HQBZ")).build();

        ResponseEntity<PasswordDto> response = testRestTemplate.exchange(request, new ParameterizedTypeReference<PasswordDto>() {});

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("HQBZ", response.getBody().getPassword());
        assertEquals("weak", response.getBody().getComplexity());
        assertNull(response.getBody().getCreated());
    }

    @Test
    void removePasswordInvalidLengthShouldReturnConstraintViolationException() throws Exception {
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

    private URI createServerAddress(String endpoint) throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/password-api" + endpoint);
    }
}