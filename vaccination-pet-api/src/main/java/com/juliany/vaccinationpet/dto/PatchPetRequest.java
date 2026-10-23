package com.juliany.vaccinationpet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.juliany.vaccinationpet.entity.Sex;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Patch parcial de pet. Propriedades ausentes não são alteradas.")
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

    @Schema(description = "Nome do pet. Se enviado, não pode ser nulo ou vazio.", example = "Mel")
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

    @Schema(description = "Espécie do pet. Se enviado, não pode ser nula ou vazia.", example = "DOG")
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

    @Schema(description = "Sexo do pet; null limpa o valor.", nullable = true, example = "FEMALE")
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

    @Schema(description = "Data de nascimento; se enviada, não pode ser nula nem futura.", example = "2022-05-10")
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

    @Schema(description = "Propriedades do tutor atualizadas individualmente.")
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

    @Schema(description = "Nome do tutor. Se enviado, não pode ser nulo ou vazio.", example = "Maria")
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

        @Schema(description = "Contato do tutor. Se enviado, não pode ser nulo ou vazio.", example = "maria@example.com")
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
