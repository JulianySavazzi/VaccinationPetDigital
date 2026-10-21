package com.juliany.vaccinationpet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
import com.juliany.vaccinationpet.repository.VaccinationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VaccinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        vaccinationRepository.deleteAllInBatch();
        petRepository.deleteAllInBatch();
    }

    @Test
    void shouldExecuteVaccinationCrud() throws Exception {
        long petId = createPet();

        String createdBody = mockMvc.perform(post("/api/v1/pets/{petId}/vaccinations", petId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createVaccinationJson("Antirrabica", "LOTE-001")))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.petId").value(petId))
            .andExpect(jsonPath("$.vaccineName").value("Antirrabica"))
            .andExpect(jsonPath("$.vaccineBatchNumber").value("LOTE-001"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long vaccinationId = objectMapper.readTree(createdBody).get("id").asLong();

        mockMvc.perform(get("/api/v1/vaccinations/{id}", vaccinationId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(vaccinationId))
            .andExpect(jsonPath("$.petId").value(petId));

        mockMvc.perform(get("/api/v1/pets/{petId}/vaccinations", petId)
                .param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(vaccinationId))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(put("/api/v1/vaccinations/{id}", vaccinationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createVaccinationJson("V10", "LOTE-002")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(vaccinationId))
            .andExpect(jsonPath("$.petId").value(petId))
            .andExpect(jsonPath("$.vaccineName").value("V10"))
            .andExpect(jsonPath("$.vaccineBatchNumber").value("LOTE-002"));

        mockMvc.perform(delete("/api/v1/vaccinations/{id}", vaccinationId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/vaccinations/{id}", vaccinationId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail")
                    .value("Vaccination not found with id: " + vaccinationId));
    }

    @Test
    void shouldRejectInvalidVaccination() throws Exception {
        long petId = createPet();

        mockMvc.perform(post("/api/v1/pets/{petId}/vaccinations", petId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "vaccineName": "",
                      "vaccineBatchNumber": "",
                      "applicationDate": "2026-01-15",
                      "expirationDate": "2026-01-14",
                      "nextVaccinationDate": "2026-01-15"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors.vaccineName").exists())
            .andExpect(jsonPath("$.fieldErrors.vaccineBatchNumber").exists())
            .andExpect(jsonPath("$.fieldErrors.expirationDateValid").exists())
            .andExpect(jsonPath("$.fieldErrors.nextVaccinationDateValid").exists());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingVaccinationForUnknownPet() throws Exception {
        mockMvc.perform(post("/api/v1/pets/{petId}/vaccinations", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createVaccinationJson("Antirrabica", "LOTE-001")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Pet not found with id: -1"));
    }

    @Test
    void shouldPreventDeletingPetWithVaccination() throws Exception {
        long petId = createPet();
        mockMvc.perform(post("/api/v1/pets/{petId}/vaccinations", petId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createVaccinationJson("Antirrabica", "LOTE-001")))
            .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/pets/{id}", petId))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Data integrity violation"));
    }

    @Test
    void shouldPaginateVaccinationsWithTwentyRecordsPerPage() throws Exception {
        long petId = createPet();
        for (int index = 1; index <= 21; index++) {
            mockMvc.perform(post("/api/v1/pets/{petId}/vaccinations", petId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createVaccinationJson("Vaccine " + index, "LOTE-" + index)))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/pets/{petId}/vaccinations", petId)
                .param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(20))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(21))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/v1/pets/{petId}/vaccinations", petId)
                .param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldRejectPageZero() throws Exception {
        long petId = createPet();

        mockMvc.perform(get("/api/v1/pets/{petId}/vaccinations", petId)
                .param("page", "0"))
            .andExpect(status().isBadRequest());
    }

    private long createPet() throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Luna",
                      "species": "DOG",
                      "sex": "FEMALE",
                      "birthdate": "2022-05-10",
                      "owner": {
                        "name": "Juliany",
                        "contact": "juliany@email.com"
                      }
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode pet = objectMapper.readTree(responseBody);
        return pet.get("id").asLong();
    }

    private String createVaccinationJson(String vaccineName, String batchNumber) {
        return """
            {
              "vaccineName": "%s",
              "vaccineBatchNumber": "%s",
              "applicationDate": "2026-01-15",
              "expirationDate": "2027-01-15",
              "nextVaccinationDate": "2027-01-15"
            }
            """.formatted(vaccineName, batchNumber);
    }
}
