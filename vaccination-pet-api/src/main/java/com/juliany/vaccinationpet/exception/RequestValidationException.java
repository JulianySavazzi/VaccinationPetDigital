package com.juliany.vaccinationpet.exception;

import java.util.Map;

public class RequestValidationException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public RequestValidationException(Map<String, String> fieldErrors) {
        super("One or more fields are invalid.");
        this.fieldErrors = Map.copyOf(fieldErrors);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
