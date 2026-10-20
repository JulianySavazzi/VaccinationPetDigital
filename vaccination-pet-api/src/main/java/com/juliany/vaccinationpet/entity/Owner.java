package com.juliany.vaccinationpet.entity;

import jakarta.validation.constraints.NotBlank;

public record Owner(
        @NotBlank String name,
        @NotBlank String contact
) {
}
