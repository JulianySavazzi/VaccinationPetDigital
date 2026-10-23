package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.service.VaccinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaccinationControllerUnitTest {

    @Mock
    private VaccinationService vaccinationService;

    private VaccinationController vaccinationController;

    @BeforeEach
    void setUp() {
        vaccinationController = new VaccinationController(vaccinationService);
    }

    @Test
    void shouldCreateVaccinationAndReturnLocation() {
        CreateVaccinationRequest request = createRequest();
        VaccinationResponse expectedResponse = createResponse();
        when(vaccinationService.create(10L, request)).thenReturn(expectedResponse);

        ResponseEntity<VaccinationResponse> response = vaccinationController.create(10L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation())
                .isEqualTo(URI.create("/api/v1/vaccinations/1"));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnAllVaccinationsForPet() {
        PageResponse<VaccinationResponse> expectedResponse = new PageResponse<>(
                List.of(createResponse()), 1, 20, 1, 1, true, true
        );
        when(vaccinationService.findAllByPetId(10L, 1)).thenReturn(expectedResponse);

        ResponseEntity<PageResponse<VaccinationResponse>> response =
                vaccinationController.findAllByPetId(10L, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnVaccinationById() {
        VaccinationResponse expectedResponse = createResponse();
        when(vaccinationService.findById(1L)).thenReturn(expectedResponse);

        ResponseEntity<VaccinationResponse> response = vaccinationController.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldUpdateVaccination() {
        PatchVaccinationRequest request = new PatchVaccinationRequest();
        request.setVaccineName("V10");
        VaccinationResponse expectedResponse = createResponse();
        when(vaccinationService.update(1L, request)).thenReturn(expectedResponse);

        ResponseEntity<VaccinationResponse> response = vaccinationController.update(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldDeleteVaccination() {
        ResponseEntity<Void> response = vaccinationController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(vaccinationService).delete(1L);
    }

    private CreateVaccinationRequest createRequest() {
        return new CreateVaccinationRequest(
                "Antirrabica",
                "LOTE-001",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2027, 1, 15),
                LocalDate.of(2027, 1, 15)
        );
    }

    private VaccinationResponse createResponse() {
        return new VaccinationResponse(
                1L,
                0L,
                10L,
                "Antirrabica",
                "LOTE-001",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2027, 1, 15),
                LocalDate.of(2027, 1, 15),
                OffsetDateTime.parse("2026-10-21T10:00:00-03:00"),
                OffsetDateTime.parse("2026-10-21T10:00:00-03:00")
        );
    }
}
