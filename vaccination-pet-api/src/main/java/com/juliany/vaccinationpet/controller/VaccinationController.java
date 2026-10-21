package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.service.VaccinationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<List<VaccinationResponse>> findAllByPetId(@PathVariable Long petId) {
        return ResponseEntity.ok(vaccinationService.findAllByPetId(petId));
    }

    @GetMapping("/vaccinations/{id}")
    public ResponseEntity<VaccinationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vaccinationService.findById(id));
    }

    @PutMapping("/vaccinations/{id}")
    public ResponseEntity<VaccinationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateVaccinationRequest request
    ) {
        return ResponseEntity.ok(vaccinationService.update(id, request));
    }

    @DeleteMapping("/vaccinations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaccinationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
