package com.juliany.vaccinationpet.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CreateVaccinationRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldAcceptValidRequest() {
        CreateVaccinationRequest request = new CreateVaccinationRequest(
                "Antirrabica",
                "LOTE-001",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2027, 1, 15),
                LocalDate.of(2027, 1, 15)
        );

        Set<ConstraintViolation<CreateVaccinationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldAcceptNullNextVaccinationDate() {
        CreateVaccinationRequest request = new CreateVaccinationRequest(
                "Antirrabica",
                "LOTE-001",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2027, 1, 15),
                null
        );

        Set<ConstraintViolation<CreateVaccinationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectBlankRequiredFieldsAndInvalidDates() {
        CreateVaccinationRequest request = new CreateVaccinationRequest(
                " ",
                "",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2026, 1, 14),
                LocalDate.of(2026, 1, 15)
        );

        Set<String> invalidFields = validator.validate(request)
                .stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(invalidFields).contains(
                "vaccineName",
                "vaccineBatchNumber",
                "expirationDateValid",
                "nextVaccinationDateValid"
        );
    }
}
