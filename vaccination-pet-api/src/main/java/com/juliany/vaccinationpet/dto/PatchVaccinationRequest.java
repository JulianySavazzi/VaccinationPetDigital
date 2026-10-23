package com.juliany.vaccinationpet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Patch parcial de vacinação. Propriedades ausentes não são alteradas.")
public class PatchVaccinationRequest {

    private String vaccineName;
    private boolean vaccineNameProvided;
    private String vaccineBatchNumber;
    private boolean vaccineBatchNumberProvided;
    private LocalDate applicationDate;
    private boolean applicationDateProvided;
    private LocalDate expirationDate;
    private boolean expirationDateProvided;
    private LocalDate nextVaccinationDate;
    private boolean nextVaccinationDateProvided;

    @Schema(description = "Nome da vacina; se enviado, não pode ser nulo ou vazio.", example = "Antirrábica")
    public String getVaccineName() {
        return vaccineName;
    }

    @JsonSetter("vaccineName")
    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
        this.vaccineNameProvided = true;
    }

    @JsonIgnore
    public boolean isVaccineNameProvided() {
        return vaccineNameProvided;
    }

    @Schema(description = "Número do lote; se enviado, não pode ser nulo ou vazio.", example = "LOTE-001")
    public String getVaccineBatchNumber() {
        return vaccineBatchNumber;
    }

    @JsonSetter("vaccineBatchNumber")
    public void setVaccineBatchNumber(String vaccineBatchNumber) {
        this.vaccineBatchNumber = vaccineBatchNumber;
        this.vaccineBatchNumberProvided = true;
    }

    @JsonIgnore
    public boolean isVaccineBatchNumberProvided() {
        return vaccineBatchNumberProvided;
    }

    @Schema(description = "Data da aplicação; se enviada, não pode ser nula.", example = "2026-01-15")
    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    @JsonSetter("applicationDate")
    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
        this.applicationDateProvided = true;
    }

    @JsonIgnore
    public boolean isApplicationDateProvided() {
        return applicationDateProvided;
    }

    @Schema(description = "Data de validade; se enviada, não pode ser nula e deve ser igual ou posterior à aplicação.", example = "2027-01-15")
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @JsonSetter("expirationDate")
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        this.expirationDateProvided = true;
    }

    @JsonIgnore
    public boolean isExpirationDateProvided() {
        return expirationDateProvided;
    }

    @Schema(
            description = "Próxima vacinação. Se omitida, mantém o valor atual; null limpa a data.",
            nullable = true,
            example = "2027-01-15"
    )
    public LocalDate getNextVaccinationDate() {
        return nextVaccinationDate;
    }

    @JsonSetter("nextVaccinationDate")
    public void setNextVaccinationDate(LocalDate nextVaccinationDate) {
        this.nextVaccinationDate = nextVaccinationDate;
        this.nextVaccinationDateProvided = true;
    }

    @JsonIgnore
    public boolean isNextVaccinationDateProvided() {
        return nextVaccinationDateProvided;
    }
}
