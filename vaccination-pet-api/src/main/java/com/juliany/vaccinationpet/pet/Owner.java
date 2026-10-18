package com.juliany.vaccinationpet.pet;

import jakarta.validation.constraints.NotBlank;

public record Owner(
        @NotBlank String name,
        @NotBlank String contact
) {
}
