package com.juliany.vaccinationpet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vaccinations")
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pet_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vaccinations_pets")
    )
    private Pet pet;

    @NotBlank
    @Size(max = 100)
    @Column(name = "vaccine_name", nullable = false, length = 100)
    private String vaccineName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "vaccine_batch_number", nullable = false, length = 100)
    private String vaccineBatchNumber;

    @NotNull
    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @NotNull
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "next_vaccination_date")
    private LocalDate nextVaccinationDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Vaccination() {
    }

    public Vaccination(
            Pet pet,
            String vaccineName,
            String vaccineBatchNumber,
            LocalDate applicationDate,
            LocalDate expirationDate,
            LocalDate nextVaccinationDate
    ) {
        this.pet = pet;
        this.vaccineName = vaccineName;
        this.vaccineBatchNumber = vaccineBatchNumber;
        this.applicationDate = applicationDate;
        this.expirationDate = expirationDate;
        this.nextVaccinationDate = nextVaccinationDate;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getVaccineBatchNumber() {
        return vaccineBatchNumber;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public LocalDate getNextVaccinationDate() {
        return nextVaccinationDate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public void setVaccineBatchNumber(String vaccineBatchNumber) {
        this.vaccineBatchNumber = vaccineBatchNumber;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setNextVaccinationDate(LocalDate nextVaccinationDate) {
        this.nextVaccinationDate = nextVaccinationDate;
    }
}
