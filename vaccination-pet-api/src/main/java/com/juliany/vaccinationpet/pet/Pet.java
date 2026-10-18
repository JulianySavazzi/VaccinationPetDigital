package com.juliany.vaccinationpet.pet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(name = "species", nullable = false, length = 50)
    private String species;

    @Size(max = 10)
    @Column(name = "sex", length = 10)
    private String sex;

    @NotNull
    @Column(name = "birthdate", nullable = false)
    private OffsetDateTime birthdate;

    @Valid
    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "owner", nullable = false, columnDefinition = "jsonb")
    private Owner owner;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Pet() {
    }

    public Pet(String name, String species, String sex, OffsetDateTime birthdate, Owner owner) {
        this.name = name;
        this.species = species;
        this.sex = sex;
        this.birthdate = birthdate;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getSex() {
        return sex;
    }

    public OffsetDateTime getBirthdate() {
        return birthdate;
    }

    public Owner getOwner() {
        return owner;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthdate(OffsetDateTime birthdate) {
        this.birthdate = birthdate;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
