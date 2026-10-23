package com.juliany.vaccinationpet.dto;

import com.juliany.vaccinationpet.entity.Sex;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description = "Dados de um pet retornados pela API.")
public record PetResponse(
        Long id,
        @Schema(description = "Versão atual usada pelo mecanismo de concorrência otimista.", example = "0")
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
