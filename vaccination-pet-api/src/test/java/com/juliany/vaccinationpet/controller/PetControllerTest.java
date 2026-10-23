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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        vaccinationRepository.deleteAllInBatch();
        petRepository.deleteAllInBatch();
    }

    @Test
    void shouldExecutePetCrud() throws Exception {
        String createdBody = mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPetJson("Luna", "Juliany")))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value("Luna"))
            .andExpect(jsonPath("$.sex").value("FEMALE"))
            .andExpect(jsonPath("$.birthdate").value("2022-05-10"))
            .andExpect(jsonPath("$.owner.name").value("Juliany"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode createdPet = objectMapper.readTree(createdBody);
        long petId = createdPet.get("id").asLong();

        mockMvc.perform(get("/api/v1/pets/{id}", petId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(petId))
            .andExpect(jsonPath("$.name").value("Luna"));

        mockMvc.perform(get("/api/v1/pets").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(petId))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("""
                    {
                      "name": "Mel",
                      "owner": { "name": "Maria" }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(petId))
            .andExpect(jsonPath("$.name").value("Mel"))
            .andExpect(jsonPath("$.species").value("DOG"))
            .andExpect(jsonPath("$.owner.name").value("Maria"))
            .andExpect(jsonPath("$.owner.contact").value("juliany@email.com"));

        mockMvc.perform(delete("/api/v1/pets/{id}", petId))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/pets/{id}", petId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void shouldRejectInvalidPet() throws Exception {
        mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "",
                      "species": "DOG",
                      "sex": "FEMALE",
                      "birthdate": "2999-05-10",
                      "owner": {
                        "name": "Juliany",
                        "contact": ""
                      }
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors.name").exists())
            .andExpect(jsonPath("$.fieldErrors.birthdate").exists())
            .andExpect(jsonPath("$.fieldErrors['owner.contact']").exists());
    }

    @Test
    void shouldReturnNotFoundForUnknownPet() throws Exception {
        mockMvc.perform(get("/api/v1/pets/{id}", -1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Pet not found with id: -1"));
    }

    @Test
    void shouldUpdateOnlyPetAndOwnerFieldsExplicitlyProvided() throws Exception {
        String body = mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPetJson("Luna", "Juliany")))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        long petId = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("""
                    {
                      "owner": { "name": "Maria" }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Luna"))
            .andExpect(jsonPath("$.owner.name").value("Maria"))
            .andExpect(jsonPath("$.owner.contact").value("juliany@email.com"));
    }

    @Test
    void shouldRejectNullForRequiredPetField() throws Exception {
        String body = mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPetJson("Luna", "Juliany")))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        long petId = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("{\"name\":null}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void shouldDistinguishProvidedOmittedAndNullPetAndOwnerProperties() throws Exception {
        String body = mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPetJson("Luna", "Juliany")))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        long petId = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("""
                    {
                      "name": "Mel",
                      "owner": { "name": "Maria" }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Mel"))
            .andExpect(jsonPath("$.owner.name").value("Maria"))
            .andExpect(jsonPath("$.owner.contact").value("juliany@email.com"));

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("""
                    {
                      "owner": { "contact": "maria@example.com" }
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Mel"))
            .andExpect(jsonPath("$.owner.name").value("Maria"))
            .andExpect(jsonPath("$.owner.contact").value("maria@example.com"));

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Mel"))
            .andExpect(jsonPath("$.owner.name").value("Maria"))
            .andExpect(jsonPath("$.owner.contact").value("maria@example.com"));

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("{\"name\":null}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name").exists());

        mockMvc.perform(patch("/api/v1/pets/{id}", petId)
                .contentType("application/merge-patch+json")
                .content("{\"owner\":{\"name\":null}}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors['owner.name']").exists());
    }

    @Test
    void shouldPaginatePetsWithTwentyRecordsPerPage() throws Exception {
        for (int index = 1; index <= 21; index++) {
            mockMvc.perform(post("/api/v1/pets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createPetJson("Pet " + index, "Juliany")))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/pets").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(20))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(21))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/v1/pets").param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.page").value(2))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldRejectPageZero() throws Exception {
        mockMvc.perform(get("/api/v1/pets").param("page", "0"))
            .andExpect(status().isBadRequest());
    }

    private String createPetJson(String petName, String ownerName) {
        return """
            {
              "name": "%s",
              "species": "DOG",
              "sex": "FEMALE",
              "birthdate": "2022-05-10",
              "owner": {
                "name": "%s",
                "contact": "juliany@email.com"
              }
            }
            """.formatted(petName, ownerName);
    }
}
