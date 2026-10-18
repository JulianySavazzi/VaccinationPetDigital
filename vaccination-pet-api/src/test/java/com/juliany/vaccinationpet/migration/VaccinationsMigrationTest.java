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
class VaccinationsMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldInsertAndFindVaccinationForExistingPet() {
        Long petId = insertPet();

        Long vaccinationId = jdbcTemplate.queryForObject(
            """
            INSERT INTO vaccinations (
                pet_id,
                vaccine_name,
                vaccine_batch_number,
                application_date,
                expiration_date,
                next_vaccination_date
            )
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """,
            Long.class,
            petId,
            "Antirrabica",
            "LOTE-2026-001",
            LocalDate.parse("2026-01-15"),
            LocalDate.parse("2027-01-15"),
            LocalDate.parse("2027-01-15")
        );

        Map<String, Object> vaccination = jdbcTemplate.queryForMap(
            """
            SELECT
                v.id,
                v.pet_id,
                v.vaccine_name,
                v.vaccine_batch_number,
                v.created_at,
                v.updated_at,
                p.name AS pet_name
            FROM vaccinations v
            JOIN pets p ON p.id = v.pet_id
            WHERE v.id = ?
            """,
            vaccinationId
        );

        assertThat(vaccinationId).isNotNull();
        assertThat(vaccination.get("pet_id")).isEqualTo(petId);
        assertThat(vaccination.get("vaccine_name")).isEqualTo("Antirrabica");
        assertThat(vaccination.get("vaccine_batch_number")).isEqualTo("LOTE-2026-001");
        assertThat(vaccination.get("pet_name")).isEqualTo("Luna");
        assertThat(vaccination.get("created_at")).isNotNull();
        assertThat(vaccination.get("updated_at")).isNotNull();
    }

    @Test
    void shouldRejectVaccinationForNonexistentPet() {
        assertThatThrownBy(() -> jdbcTemplate.update(
            """
            INSERT INTO vaccinations (
                pet_id,
                vaccine_name,
                vaccine_batch_number,
                application_date,
                expiration_date,
                next_vaccination_date
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """,
            -1L,
            "Antirrabica",
            "LOTE-2026-002",
            LocalDate.parse("2026-01-15"),
            LocalDate.parse("2027-01-15"),
            LocalDate.parse("2027-01-15")
        ))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("fk_vaccinations_pets");
    }

    private Long insertPet() {
        String ownerJson = """
            {
                "name": "Juliany",
                "contact": "juliany@email.com"
            }
            """;

        return jdbcTemplate.queryForObject(
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
    }
}
