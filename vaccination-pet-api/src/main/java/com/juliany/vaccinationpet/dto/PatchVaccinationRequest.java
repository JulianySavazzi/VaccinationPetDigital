package com.juliany.vaccinationpet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.LocalDate;

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
