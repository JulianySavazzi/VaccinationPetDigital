package com.juliany.vaccinationpet.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record VaccinationResponse(
        Long id,
        Long version,
        Long petId,
        String vaccineName,
        String vaccineBatchNumber,
        LocalDate applicationDate,
        LocalDate expirationDate,
        LocalDate nextVaccinationDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
