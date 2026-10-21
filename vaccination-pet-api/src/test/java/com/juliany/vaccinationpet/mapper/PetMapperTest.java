package com.juliany.vaccinationpet.mapper;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.entity.Owner;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PetMapperTest {

    private PetMapper petMapper;

    @BeforeEach
    void setUp() {
        petMapper = new PetMapper();
    }

    @Test
    void shouldMapRequestToEntity() {
        CreatePetRequest request = createRequest("Luna", "Juliany");

        Pet pet = petMapper.toEntity(request);

        assertThat(pet.getId()).isNull();
        assertThat(pet.getName()).isEqualTo("Luna");
        assertThat(pet.getSpecies()).isEqualTo("DOG");
        assertThat(pet.getSex()).isEqualTo(Sex.FEMALE);
        assertThat(pet.getBirthdate()).isEqualTo(LocalDate.of(2022, 5, 10));
        assertThat(pet.getOwner().name()).isEqualTo("Juliany");
        assertThat(pet.getOwner().contact()).isEqualTo("juliany@email.com");
    }

    @Test
    void shouldMapEntityToResponse() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-10-21T10:00:00-03:00");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-10-21T11:00:00-03:00");
        Pet pet = createPet("Luna", "Juliany");
        ReflectionTestUtils.setField(pet, "id", 10L);
        ReflectionTestUtils.setField(pet, "createdAt", createdAt);
        ReflectionTestUtils.setField(pet, "updatedAt", updatedAt);

        PetResponse response = petMapper.toResponse(pet);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Luna");
        assertThat(response.species()).isEqualTo("DOG");
        assertThat(response.sex()).isEqualTo(Sex.FEMALE);
        assertThat(response.birthdate()).isEqualTo(LocalDate.of(2022, 5, 10));
        assertThat(response.owner().name()).isEqualTo("Juliany");
        assertThat(response.owner().contact()).isEqualTo("juliany@email.com");
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        Pet pet = createPet("Luna", "Juliany");
        CreatePetRequest request = createRequest("Mel", "Maria");

        petMapper.updateEntity(pet, request);

        assertThat(pet.getName()).isEqualTo("Mel");
        assertThat(pet.getSpecies()).isEqualTo("DOG");
        assertThat(pet.getSex()).isEqualTo(Sex.FEMALE);
        assertThat(pet.getBirthdate()).isEqualTo(LocalDate.of(2022, 5, 10));
        assertThat(pet.getOwner().name()).isEqualTo("Maria");
        assertThat(pet.getOwner().contact()).isEqualTo("juliany@email.com");
    }

    private CreatePetRequest createRequest(String petName, String ownerName) {
        return new CreatePetRequest(
                petName,
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new CreatePetRequest.OwnerRequest(ownerName, "juliany@email.com")
        );
    }

    private Pet createPet(String petName, String ownerName) {
        return new Pet(
                petName,
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new Owner(ownerName, "juliany@email.com")
        );
    }
}
