package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.service.VaccinationService;
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
@RequestMapping("/api/v1")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @PostMapping("/pets/{petId}/vaccinations")
    public ResponseEntity<VaccinationResponse> create(
            @PathVariable Long petId,
            @Valid @RequestBody CreateVaccinationRequest request
    ) {
        VaccinationResponse response = vaccinationService.create(petId, request);
        URI location = URI.create("/api/v1/vaccinations/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/pets/{petId}/vaccinations")
    public ResponseEntity<PageResponse<VaccinationResponse>> findAllByPetId(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "1") @Positive int page
    ) {
        return ResponseEntity.ok(vaccinationService.findAllByPetId(petId, page));
    }

    @GetMapping("/vaccinations/{id}")
    public ResponseEntity<VaccinationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vaccinationService.findById(id));
    }

    @PatchMapping(value = "/vaccinations/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<VaccinationResponse> update(
            @PathVariable Long id,
            @RequestBody PatchVaccinationRequest request
    ) {
        return ResponseEntity.ok(vaccinationService.update(id, request));
    }

    @DeleteMapping("/vaccinations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaccinationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
