package com.juliany.vaccinationpet.dto;

import com.juliany.vaccinationpet.entity.Sex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePetRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 50)
        String species,

        Sex sex,

        @NotNull
        @PastOrPresent
        LocalDate birthdate,

        @Valid
        @NotNull
        OwnerRequest owner
) {

    public record OwnerRequest(
            @NotBlank String name,
            @NotBlank String contact
    ) {
    }
}
