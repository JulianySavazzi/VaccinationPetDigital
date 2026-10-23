package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchPetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody CreatePetRequest request) {
        PetResponse response = petService.create(request);
        URI location = URI.create("/api/v1/pets/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PetResponse>> findAll(
            @RequestParam(defaultValue = "1") @Positive int page
    ) {
        return ResponseEntity.ok(petService.findAll(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.findById(id));
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<PetResponse> update(
            @PathVariable Long id,
            @RequestBody PatchPetRequest request
    ) {
        return ResponseEntity.ok(petService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
