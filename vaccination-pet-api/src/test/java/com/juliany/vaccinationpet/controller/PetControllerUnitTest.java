package com.juliany.vaccinationpet.controller;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.entity.Sex;
import com.juliany.vaccinationpet.service.PetService;
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
class PetControllerUnitTest {

    @Mock
    private PetService petService;

    private PetController petController;

    @BeforeEach
    void setUp() {
        petController = new PetController(petService);
    }

    @Test
    void shouldCreatePetAndReturnLocation() {
        CreatePetRequest request = createRequest();
        PetResponse expectedResponse = createResponse();
        when(petService.create(request)).thenReturn(expectedResponse);

        ResponseEntity<PetResponse> response = petController.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("/api/v1/pets/1"));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnAllPets() {
        PageResponse<PetResponse> expectedResponse = new PageResponse<>(
                List.of(createResponse()), 1, 20, 1, 1, true, true
        );
        when(petService.findAll(1)).thenReturn(expectedResponse);

        ResponseEntity<PageResponse<PetResponse>> response = petController.findAll(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnPetById() {
        PetResponse expectedResponse = createResponse();
        when(petService.findById(1L)).thenReturn(expectedResponse);

        ResponseEntity<PetResponse> response = petController.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldUpdatePet() {
        CreatePetRequest request = createRequest();
        PetResponse expectedResponse = createResponse();
        when(petService.update(1L, request)).thenReturn(expectedResponse);

        ResponseEntity<PetResponse> response = petController.update(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void shouldDeletePet() {
        ResponseEntity<Void> response = petController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(petService).delete(1L);
    }

    private CreatePetRequest createRequest() {
        return new CreatePetRequest(
                "Luna",
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new CreatePetRequest.OwnerRequest("Juliany", "juliany@email.com")
        );
    }

    private PetResponse createResponse() {
        return new PetResponse(
                1L,
                0L,
                "Luna",
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new PetResponse.OwnerResponse("Juliany", "juliany@email.com"),
                OffsetDateTime.parse("2026-10-21T10:00:00-03:00"),
                OffsetDateTime.parse("2026-10-21T10:00:00-03:00")
        );
    }
}
