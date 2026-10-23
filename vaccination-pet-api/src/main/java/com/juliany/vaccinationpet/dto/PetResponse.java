package com.juliany.vaccinationpet.dto;

import com.juliany.vaccinationpet.entity.Sex;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PetResponse(
        Long id,
        Long version,
        String name,
        String species,
        Sex sex,
        LocalDate birthdate,
        OwnerResponse owner,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public record OwnerResponse(
            String name,
            String contact
    ) {
    }
}
