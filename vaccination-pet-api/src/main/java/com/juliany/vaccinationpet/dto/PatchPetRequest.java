package com.juliany.vaccinationpet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.juliany.vaccinationpet.entity.Sex;

import java.time.LocalDate;

public class PatchPetRequest {

    private String name;
    private boolean nameProvided;
    private String species;
    private boolean speciesProvided;
    private Sex sex;
    private boolean sexProvided;
    private LocalDate birthdate;
    private boolean birthdateProvided;
    private PatchOwnerRequest owner;
    private boolean ownerProvided;

    public String getName() {
        return name;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
        this.nameProvided = true;
    }

    @JsonIgnore
    public boolean isNameProvided() {
        return nameProvided;
    }

    public String getSpecies() {
        return species;
    }

    @JsonSetter("species")
    public void setSpecies(String species) {
        this.species = species;
        this.speciesProvided = true;
    }

    @JsonIgnore
    public boolean isSpeciesProvided() {
        return speciesProvided;
    }

    public Sex getSex() {
        return sex;
    }

    @JsonSetter("sex")
    public void setSex(Sex sex) {
        this.sex = sex;
        this.sexProvided = true;
    }

    @JsonIgnore
    public boolean isSexProvided() {
        return sexProvided;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    @JsonSetter("birthdate")
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        this.birthdateProvided = true;
    }

    @JsonIgnore
    public boolean isBirthdateProvided() {
        return birthdateProvided;
    }

    public PatchOwnerRequest getOwner() {
        return owner;
    }

    @JsonSetter("owner")
    public void setOwner(PatchOwnerRequest owner) {
        this.owner = owner;
        this.ownerProvided = true;
    }

    @JsonIgnore
    public boolean isOwnerProvided() {
        return ownerProvided;
    }

    public static class PatchOwnerRequest {

        private String name;
        private boolean nameProvided;
        private String contact;
        private boolean contactProvided;

        public String getName() {
            return name;
        }

        @JsonSetter("name")
        public void setName(String name) {
            this.name = name;
            this.nameProvided = true;
        }

        @JsonIgnore
        public boolean isNameProvided() {
            return nameProvided;
        }

        public String getContact() {
            return contact;
        }

        @JsonSetter("contact")
        public void setContact(String contact) {
            this.contact = contact;
            this.contactProvided = true;
        }

        @JsonIgnore
        public boolean isContactProvided() {
            return contactProvided;
        }
    }
}
