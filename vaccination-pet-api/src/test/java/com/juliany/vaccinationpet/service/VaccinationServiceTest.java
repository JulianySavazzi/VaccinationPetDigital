package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.entity.Owner;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Sex;
import com.juliany.vaccinationpet.entity.Vaccination;
import com.juliany.vaccinationpet.exception.ResourceNotFoundException;
import com.juliany.vaccinationpet.mapper.VaccinationMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
import com.juliany.vaccinationpet.repository.VaccinationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaccinationServiceTest {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VaccinationMapper vaccinationMapper;

    @InjectMocks
    private VaccinationService vaccinationService;

    @Test
    void shouldCreateVaccinationForExistingPet() {
        Pet pet = createPet();
        CreateVaccinationRequest request = createRequest();
        Vaccination vaccination = createVaccination(pet);
        VaccinationResponse expectedResponse = createResponse(1L);
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(vaccinationMapper.toEntity(pet, request)).thenReturn(vaccination);
        when(vaccinationRepository.saveAndFlush(vaccination)).thenReturn(vaccination);
        when(vaccinationMapper.toResponse(vaccination)).thenReturn(expectedResponse);

        VaccinationResponse response = vaccinationService.create(10L, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(vaccinationRepository).saveAndFlush(vaccination);
    }

    @Test
    void shouldNotCreateVaccinationForUnknownPet() {
        CreateVaccinationRequest request = createRequest();
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vaccinationService.create(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pet not found with id: 99");

        verify(vaccinationRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldFindAllVaccinationsForPet() {
        Pet pet = createPet();
        Vaccination first = createVaccination(pet);
        Vaccination second = createVaccination(pet);
        VaccinationResponse firstResponse = createResponse(1L);
        VaccinationResponse secondResponse = createResponse(2L);
        Pageable pageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.ASC, "id")
        );
        when(petRepository.findById(10L)).thenReturn(Optional.of(pet));
        when(vaccinationRepository.findAllByPetId(10L, pageable))
                .thenReturn(new PageImpl<>(List.of(first, second), pageable, 2));
        when(vaccinationMapper.toResponse(first)).thenReturn(firstResponse);
        when(vaccinationMapper.toResponse(second)).thenReturn(secondResponse);

        PageResponse<VaccinationResponse> response = vaccinationService.findAllByPetId(10L, 1);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    @Test
    void shouldNotListVaccinationsForUnknownPet() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vaccinationService.findAllByPetId(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pet not found with id: 99");

        verify(vaccinationRepository, never()).findAllByPetId(any(), any());
    }

    @Test
    void shouldFindVaccinationById() {
        Vaccination vaccination = createVaccination(createPet());
        VaccinationResponse expectedResponse = createResponse(1L);
        when(vaccinationRepository.findById(1L)).thenReturn(Optional.of(vaccination));
        when(vaccinationMapper.toResponse(vaccination)).thenReturn(expectedResponse);

        VaccinationResponse response = vaccinationService.findById(1L);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldThrowWhenVaccinationIsNotFound() {
        when(vaccinationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vaccinationService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vaccination not found with id: 99");
    }

    @Test
    void shouldUpdateVaccination() {
        CreateVaccinationRequest request = createRequest();
        Vaccination vaccination = createVaccination(createPet());
        VaccinationResponse expectedResponse = createResponse(1L);
        when(vaccinationRepository.findById(1L)).thenReturn(Optional.of(vaccination));
        when(vaccinationRepository.saveAndFlush(vaccination)).thenReturn(vaccination);
        when(vaccinationMapper.toResponse(vaccination)).thenReturn(expectedResponse);

        VaccinationResponse response = vaccinationService.update(1L, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(vaccinationMapper).updateEntity(vaccination, request);
        verify(vaccinationRepository).saveAndFlush(vaccination);
    }

    @Test
    void shouldDeleteVaccination() {
        Vaccination vaccination = createVaccination(createPet());
        when(vaccinationRepository.findById(1L)).thenReturn(Optional.of(vaccination));

        vaccinationService.delete(1L);

        verify(vaccinationRepository).delete(vaccination);
        verify(vaccinationRepository).flush();
    }

    @Test
    void shouldNotDeleteUnknownVaccination() {
        when(vaccinationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vaccinationService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vaccination not found with id: 99");

        verify(vaccinationRepository, never()).delete(any());
        verify(vaccinationRepository, never()).flush();
    }

    private Pet createPet() {
        return new Pet(
                "Luna",
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new Owner("Juliany", "juliany@email.com")
        );
    }

    private Vaccination createVaccination(Pet pet) {
        return new Vaccination(
                pet,
                "Antirrabica",
                "LOTE-001",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2027, 1, 15),
                LocalDate.of(2027, 1, 15)
        );
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

    private VaccinationResponse createResponse(Long id) {
        return new VaccinationResponse(
                id,
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
