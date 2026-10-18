package com.juliany.vaccinationpet.migration;

import java.time.LocalDate;
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
            LocalDate.parse("2022-05-10"),
            ownerJson
        );

        Map<String, Object> pet = jdbcTemplate.queryForMap(
            """
            SELECT
                id,
                name,
                species,
                sex,
                birthdate,
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
        assertThat(((java.sql.Date) pet.get("birthdate")).toLocalDate())
            .isEqualTo(LocalDate.parse("2022-05-10"));
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
            LocalDate.parse("2022-05-10"),
            ownerWithoutContact
        ))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("chk_pets_owner");
    }

    @Test
    void shouldRejectPetWithInvalidSex() {
        String ownerJson = """
            {
                "name": "Juliany",
                "contact": "juliany@email.com"
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
            "INVALID",
            LocalDate.parse("2022-05-10"),
            ownerJson
        ))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("chk_pets_sex");
    }
}
