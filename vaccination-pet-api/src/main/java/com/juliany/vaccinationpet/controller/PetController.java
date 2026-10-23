package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.ApiErrorResponse;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchPetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/v1/pets")
@Tag(name = "Pets", description = "Cadastro e consulta de pets.")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar pet", description = "Cria um pet e retorna sua representação e o cabeçalho Location.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pet cadastrado.", content = @Content(schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Corpo inválido ou campos fora das regras de validação.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PetResponse> create(@Valid @RequestBody CreatePetRequest request) {
        PetResponse response = petService.create(request);
        URI location = URI.create("/api/v1/pets/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pets", description = "Lista os pets em ordem crescente de identificador, com 20 itens por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "O número da página deve ser maior ou igual a 1.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<PetResponse>> findAll(
            @Parameter(description = "Página solicitada; a primeira página é 1.", schema = @Schema(defaultValue = "1", minimum = "1"))
            @RequestParam(defaultValue = "1") @Positive int page
    ) {
        return ResponseEntity.ok(petService.findAll(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pet", description = "Busca um pet pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Pet não encontrado.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PetResponse> findById(
            @Parameter(description = "Identificador do pet.", example = "42") @PathVariable Long id
    ) {
        return ResponseEntity.ok(petService.findById(id));
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    @Operation(
            summary = "Atualizar parcialmente um pet",
            description = "Altera somente as propriedades enviadas. Propriedades ausentes permanecem iguais; propriedades anuláveis enviadas como null são limpas. As propriedades name e contact de owner são atualizadas independentemente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pet atualizado."),
            @ApiResponse(responseCode = "400", description = "Patch inválido ou valor inválido para um campo.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "O pet foi alterado concorrentemente.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PetResponse> update(
            @Parameter(description = "Identificador do pet.", example = "42") @PathVariable Long id,
            @RequestBody PatchPetRequest request
    ) {
        return ResponseEntity.ok(petService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pet", description = "Exclui o pet se não houver vacinações relacionadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pet excluído.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir um pet com vacinações relacionadas.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do pet.", example = "42") @PathVariable Long id
    ) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
