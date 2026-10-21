package com.juliany.vaccinationpet.mapper;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.entity.Owner;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Sex;
import com.juliany.vaccinationpet.entity.Vaccination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VaccinationMapperTest {

    private VaccinationMapper vaccinationMapper;

    @BeforeEach
    void setUp() {
        vaccinationMapper = new VaccinationMapper();
    }

    @Test
    void shouldMapRequestToEntity() {
        Pet pet = createPet(1L);
        CreateVaccinationRequest request = createRequest("Antirrabica", "LOTE-001");

        Vaccination vaccination = vaccinationMapper.toEntity(pet, request);

        assertThat(vaccination.getId()).isNull();
        assertThat(vaccination.getPet()).isSameAs(pet);
        assertThat(vaccination.getVaccineName()).isEqualTo("Antirrabica");
        assertThat(vaccination.getVaccineBatchNumber()).isEqualTo("LOTE-001");
        assertThat(vaccination.getApplicationDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(vaccination.getExpirationDate()).isEqualTo(LocalDate.of(2027, 1, 15));
        assertThat(vaccination.getNextVaccinationDate()).isEqualTo(LocalDate.of(2027, 1, 15));
    }

    @Test
    void shouldMapEntityToResponse() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-10-21T10:00:00-03:00");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-10-21T11:00:00-03:00");
        Vaccination vaccination = createVaccination(createPet(1L));
        ReflectionTestUtils.setField(vaccination, "id", 10L);
        ReflectionTestUtils.setField(vaccination, "createdAt", createdAt);
        ReflectionTestUtils.setField(vaccination, "updatedAt", updatedAt);

        VaccinationResponse response = vaccinationMapper.toResponse(vaccination);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.petId()).isEqualTo(1L);
        assertThat(response.vaccineName()).isEqualTo("Antirrabica");
        assertThat(response.vaccineBatchNumber()).isEqualTo("LOTE-001");
        assertThat(response.applicationDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(response.expirationDate()).isEqualTo(LocalDate.of(2027, 1, 15));
        assertThat(response.nextVaccinationDate()).isEqualTo(LocalDate.of(2027, 1, 15));
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldUpdateEntityWithoutChangingPet() {
        Pet pet = createPet(1L);
        Vaccination vaccination = createVaccination(pet);
        CreateVaccinationRequest request = createRequest("V10", "LOTE-002");

        vaccinationMapper.updateEntity(vaccination, request);

        assertThat(vaccination.getPet()).isSameAs(pet);
        assertThat(vaccination.getVaccineName()).isEqualTo("V10");
        assertThat(vaccination.getVaccineBatchNumber()).isEqualTo("LOTE-002");
        assertThat(vaccination.getApplicationDate()).isEqualTo(request.applicationDate());
        assertThat(vaccination.getExpirationDate()).isEqualTo(request.expirationDate());
        assertThat(vaccination.getNextVaccinationDate()).isEqualTo(request.nextVaccinationDate());
    }

    private Pet createPet(Long id) {
        Pet pet = new Pet(
                "Luna",
                "DOG",
                Sex.FEMALE,
                LocalDate.of(2022, 5, 10),
                new Owner("Juliany", "juliany@email.com")
        );
        ReflectionTestUtils.setField(pet, "id", id);
        return pet;
    }

    private Vaccination createVaccination(Pet pet) {
        return vaccinationMapper.toEntity(pet, createRequest("Antirrabica", "LOTE-001"));
    }

    private CreateVaccinationRequest createRequest(String vaccineName, String batchNumber) {
        return new CreateVaccinationRequest(
                vaccineName,
                batchNumber,
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2027, 1, 15),
                LocalDate.of(2027, 1, 15)
        );
    }
}
