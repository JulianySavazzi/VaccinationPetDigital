package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.ApiErrorResponse;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.service.VaccinationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Vaccinations", description = "Cadastro e gerenciamento de vacinações dos pets.")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @PostMapping("/pets/{petId}/vaccinations")
    @Operation(summary = "Cadastrar vacinação", description = "Registra uma vacinação para o pet informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vacinação cadastrada.", content = @Content(schema = @Schema(implementation = VaccinationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Corpo inválido ou campos fora das regras de validação.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<VaccinationResponse> create(
            @Parameter(description = "Identificador do pet que recebeu a vacinação.", example = "42") @PathVariable Long petId,
            @Valid @RequestBody CreateVaccinationRequest request
    ) {
        VaccinationResponse response = vaccinationService.create(petId, request);
        URI location = URI.create("/api/v1/vaccinations/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/pets/{petId}/vaccinations")
    @Operation(summary = "Listar vacinações do pet", description = "Lista as vacinações do pet em ordem crescente de identificador, com 20 itens por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "O número da página deve ser maior ou igual a 1.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<VaccinationResponse>> findAllByPetId(
            @Parameter(description = "Identificador do pet.", example = "42") @PathVariable Long petId,
            @Parameter(description = "Página solicitada; a primeira página é 1.", schema = @Schema(defaultValue = "1", minimum = "1"))
            @RequestParam(defaultValue = "1") @Positive int page
    ) {
        return ResponseEntity.ok(vaccinationService.findAllByPetId(petId, page));
    }

    @GetMapping("/vaccinations/{id}")
    @Operation(summary = "Consultar vacinação", description = "Busca uma vacinação pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Vacinação não encontrada.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<VaccinationResponse> findById(
            @Parameter(description = "Identificador da vacinação.", example = "15") @PathVariable Long id
    ) {
        return ResponseEntity.ok(vaccinationService.findById(id));
    }

    @PatchMapping(value = "/vaccinations/{id}", consumes = "application/merge-patch+json")
    @Operation(
            summary = "Atualizar parcialmente uma vacinação",
            description = "Altera somente as propriedades enviadas. Propriedades ausentes permanecem iguais; enviar nextVaccinationDate como null limpa a data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vacinação atualizada."),
            @ApiResponse(responseCode = "400", description = "Patch inválido ou valor inválido para um campo.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vacinação não encontrada.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A vacinação foi alterada concorrentemente.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<VaccinationResponse> update(
            @Parameter(description = "Identificador da vacinação.", example = "15") @PathVariable Long id,
            @RequestBody PatchVaccinationRequest request
    ) {
        return ResponseEntity.ok(vaccinationService.update(id, request));
    }

    @DeleteMapping("/vaccinations/{id}")
    @Operation(summary = "Excluir vacinação", description = "Exclui uma vacinação pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vacinação excluída.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vacinação não encontrada.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador da vacinação.", example = "15") @PathVariable Long id
    ) {
        vaccinationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
