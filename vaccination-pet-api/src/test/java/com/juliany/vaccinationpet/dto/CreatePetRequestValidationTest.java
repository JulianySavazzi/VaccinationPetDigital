package com.juliany.vaccinationpet.dto;

import com.juliany.vaccinationpet.entity.Sex;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePetRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldAcceptValidRequest() {
        CreatePetRequest request = new CreatePetRequest(
                "Luna",
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new CreatePetRequest.OwnerRequest("Juliany", "juliany@email.com")
        );

        Set<ConstraintViolation<CreatePetRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectInvalidRequestAndNestedOwner() {
        CreatePetRequest request = new CreatePetRequest(
                " ",
                "",
                Sex.FEMALE,
                LocalDate.now().plusDays(1),
                new CreatePetRequest.OwnerRequest("Juliany", " ")
        );

        Set<String> invalidFields = validator.validate(request)
                .stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());

        assertThat(invalidFields).contains(
                "name",
                "species",
                "birthdate",
                "owner.contact"
        );
    }
}
