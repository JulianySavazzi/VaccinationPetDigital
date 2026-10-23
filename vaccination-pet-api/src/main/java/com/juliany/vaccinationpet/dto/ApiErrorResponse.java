package com.juliany.vaccinationpet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "ApiError", description = "Formato dos erros retornados pela API.")
public record ApiErrorResponse(
        @Schema(description = "URI do tipo do problema.", example = "about:blank")
        String type,

        @Schema(description = "Título resumido do problema.", example = "Validation failed")
        String title,

        @Schema(description = "Código HTTP.", example = "400")
        Integer status,

        @Schema(description = "Descrição do problema.", example = "One or more fields are invalid.")
        String detail,

        @Schema(description = "Caminho da requisição que gerou o problema.", example = "/api/v1/pets/42")
        String path,

        @Schema(description = "Erros por campo; presente quando a validação dos dados falha.")
        Map<String, String> fieldErrors
) {
}
