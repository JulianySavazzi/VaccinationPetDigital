package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchPetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.entity.Owner;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Sex;
import com.juliany.vaccinationpet.exception.ResourceNotFoundException;
import com.juliany.vaccinationpet.mapper.PetMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @Mock
    private RequestValidationService requestValidationService;

    @InjectMocks
    private PetService petService;

    @Test
    void shouldCreatePet() {
        CreatePetRequest request = createRequest();
        Pet pet = createPet();
        PetResponse expectedResponse = createResponse(1L, "Luna");
        when(petMapper.toEntity(request)).thenReturn(pet);
        when(petRepository.saveAndFlush(pet)).thenReturn(pet);
        when(petMapper.toResponse(pet)).thenReturn(expectedResponse);

        PetResponse response = petService.create(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(petMapper).toEntity(request);
        verify(petRepository).saveAndFlush(pet);
        verify(petMapper).toResponse(pet);
    }

    @Test
    void shouldFindAllPetsOrderedById() {
        Pet firstPet = createPet();
        Pet secondPet = createPet();
        PetResponse firstResponse = createResponse(1L, "Luna");
        PetResponse secondResponse = createResponse(2L, "Mel");
        Pageable pageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.ASC, "id")
        );
        when(petRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(firstPet, secondPet), pageable, 2));
        when(petMapper.toResponse(firstPet)).thenReturn(firstResponse);
        when(petMapper.toResponse(secondPet)).thenReturn(secondResponse);

        PageResponse<PetResponse> response = petService.findAll(1);

        assertThat(response.content()).containsExactly(firstResponse, secondResponse);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();
        verify(petRepository).findAll(pageable);
    }

    @Test
    void shouldFindPetById() {
        Pet pet = createPet();
        PetResponse expectedResponse = createResponse(1L, "Luna");
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petMapper.toResponse(pet)).thenReturn(expectedResponse);

        PetResponse response = petService.findById(1L);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldThrowWhenPetIsNotFound() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pet not found with id: 99");

        verify(petMapper, never()).toResponse(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldUpdatePet() {
        PatchPetRequest request = new PatchPetRequest();
        request.setName("Luna");
        Pet pet = createPet();
        PetResponse expectedResponse = createResponse(1L, "Luna");
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.saveAndFlush(pet)).thenReturn(pet);
        when(petMapper.toResponse(pet)).thenReturn(expectedResponse);

        PetResponse response = petService.update(1L, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(requestValidationService).validate(org.mockito.ArgumentMatchers.any(CreatePetRequest.class));
        verify(petMapper).updateEntity(org.mockito.ArgumentMatchers.eq(pet),
                org.mockito.ArgumentMatchers.any(CreatePetRequest.class));
        verify(petRepository).saveAndFlush(pet);
    }

    @Test
    void shouldDeletePet() {
        Pet pet = createPet();
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        petService.delete(1L);

        verify(petRepository).delete(pet);
        verify(petRepository).flush();
    }

    @Test
    void shouldNotDeleteUnknownPet() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pet not found with id: 99");

        verify(petRepository, never()).delete(org.mockito.ArgumentMatchers.any());
        verify(petRepository, never()).flush();
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

    private Pet createPet() {
        return new Pet(
                "Luna",
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new Owner("Juliany", "juliany@email.com")
        );
    }

    private PetResponse createResponse(Long id, String name) {
        return new PetResponse(
                id,
                0L,
                name,
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new PetResponse.OwnerResponse("Juliany", "juliany@email.com"),
                OffsetDateTime.parse("2026-10-21T10:00:00-03:00"),
                OffsetDateTime.parse("2026-10-21T10:00:00-03:00")
        );
    }
}
