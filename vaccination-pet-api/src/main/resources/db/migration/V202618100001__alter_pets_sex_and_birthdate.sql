ALTER TABLE pets
    ADD CONSTRAINT chk_pets_sex
        CHECK (sex IS NULL OR sex IN ('MALE', 'FEMALE'));

ALTER TABLE pets
    ALTER COLUMN birthdate TYPE DATE
    USING (birthdate AT TIME ZONE 'America/Sao_Paulo')::DATE;
