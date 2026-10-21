package com.juliany.vaccinationpet.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/pets/99");
    }

    @Test
    void shouldHandleResourceNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException(
                "Pet not found with id: 99"
        );

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Resource not found");
        assertThat(response.getBody().getDetail()).isEqualTo("Pet not found with id: 99");
        assertThat(response.getBody().getProperties()).containsEntry("path", "/api/v1/pets/99");
    }

    @Test
    void shouldHandleValidationErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "name", "must not be blank"),
                new FieldError("request", "owner.contact", "must not be blank")
        ));

        ResponseEntity<ProblemDetail> response = handler.handleValidation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Validation failed");
        assertThat(response.getBody().getProperties()).isNotNull();
        assertThat(response.getBody().getProperties().get("fieldErrors"))
                .isEqualTo(Map.of(
                        "name", "must not be blank",
                        "owner.contact", "must not be blank"
                ));
    }

    @Test
    void shouldHandleUnreadableRequestBody() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "Invalid JSON",
                new MockHttpInputMessage(new byte[0])
        );

        ResponseEntity<ProblemDetail> response = handler.handleUnreadableMessage(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Invalid request body");
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Constraint violation"
        );

        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrityViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Data integrity violation");
    }
}
