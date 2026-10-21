package com.juliany.vaccinationpet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateVaccinationRequest(
        @NotBlank
        @Size(max = 100)
        String vaccineName,

        @NotBlank
        @Size(max = 100)
        String vaccineBatchNumber,

        @NotNull
        LocalDate applicationDate,

        @NotNull
        LocalDate expirationDate,

        LocalDate nextVaccinationDate
) {

    @JsonIgnore
    @AssertTrue(message = "must be on or after applicationDate")
    public boolean isExpirationDateValid() {
        return applicationDate == null
                || expirationDate == null
                || !expirationDate.isBefore(applicationDate);
    }

    @JsonIgnore
    @AssertTrue(message = "must be after applicationDate")
    public boolean isNextVaccinationDateValid() {
        return applicationDate == null
                || nextVaccinationDate == null
                || nextVaccinationDate.isAfter(applicationDate);
    }
}
