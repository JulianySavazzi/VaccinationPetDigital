package com.juliany.vaccinationpet.dto;

import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de resultados da listagem.")
public record PageResponse<T>(
        @Schema(description = "Registros nesta página.")
        List<T> content,
        @Schema(description = "Página atual, começando em 1.", example = "1")
        int page,
        @Schema(description = "Número máximo de registros por página.", example = "20")
        int size,
        @Schema(description = "Quantidade total de registros.", example = "42")
        long totalElements,
        @Schema(description = "Quantidade total de páginas.", example = "3")
        int totalPages,
        @Schema(description = "Indica se esta é a primeira página.")
        boolean first,
        @Schema(description = "Indica se esta é a última página.")
        boolean last
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                List.copyOf(page.getContent()),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
