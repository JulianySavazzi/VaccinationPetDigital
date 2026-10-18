package com.juliany.vaccinationpet.migration;

import java.time.OffsetDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PetsMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertAndFindPet() {
        String ownerJson = """
            {
                "name": "Juliany",
                "contact": "juliany@email.com"
            }
            """;

        Long petId = jdbcTemplate.queryForObject(
            """
            INSERT INTO pets (
                name,
                species,
                sex,
                birthdate,
                owner
            )
            VALUES (?, ?, ?, ?, CAST(? AS JSONB))
            RETURNING id
            """,
            Long.class,
            "Luna",
            "DOG",
            "FEMALE",
            OffsetDateTime.parse("2022-05-10T00:00:00Z"),
            ownerJson
        );

        Map<String, Object> pet = jdbcTemplate.queryForMap(
            """
            SELECT
                id,
                name,
                species,
                sex,
                owner ->> 'name' AS owner_name,
                owner ->> 'contact' AS owner_contact,
                created_at,
                updated_at
            FROM pets
            WHERE id = ?
            """,
            petId
        );

        assertThat(petId).isNotNull();
        assertThat(pet.get("name")).isEqualTo("Luna");
        assertThat(pet.get("species")).isEqualTo("DOG");
        assertThat(pet.get("sex")).isEqualTo("FEMALE");
        assertThat(pet.get("owner_name")).isEqualTo("Juliany");
        assertThat(pet.get("owner_contact")).isEqualTo("juliany@email.com");
        assertThat(pet.get("created_at")).isNotNull();
        assertThat(pet.get("updated_at")).isNotNull();
    }

    @Test
    void shouldRejectPetWhenOwnerDoesNotHaveContact() {
        String ownerWithoutContact = """
            {
                "name": "Juliany"
            }
            """;

        assertThatThrownBy(() -> jdbcTemplate.update(
            """
            INSERT INTO pets (
                name,
                species,
                sex,
                birthdate,
                owner
            )
            VALUES (?, ?, ?, ?, CAST(? AS JSONB))
            """,
            "Luna",
            "DOG",
            "FEMALE",
            OffsetDateTime.parse("2022-05-10T00:00:00Z"),
            ownerWithoutContact
        ))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("chk_pets_owner");
    }
}
