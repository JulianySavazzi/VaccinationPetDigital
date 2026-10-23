package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.exception.RequestValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RequestValidationService {

    private final Validator validator;

    public RequestValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (violations.isEmpty()) {
            return;
        }

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        violations.forEach(violation -> fieldErrors.putIfAbsent(
                violation.getPropertyPath().toString(),
                violation.getMessage()
        ));
        throw new RequestValidationException(fieldErrors);
    }
}
