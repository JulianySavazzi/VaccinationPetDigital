package com.juliany.vaccinationpet.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma vacinação retornados pela API.")
public record VaccinationResponse(
        Long id,
        @Schema(description = "Versão atual usada pelo mecanismo de concorrência otimista.", example = "0")
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
