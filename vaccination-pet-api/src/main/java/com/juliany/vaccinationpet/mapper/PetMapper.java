package com.juliany.vaccinationpet.mapper;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.entity.Owner;
import com.juliany.vaccinationpet.entity.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public Pet toEntity(CreatePetRequest request) {
        return new Pet(
                request.name(),
                request.species(),
                request.sex(),
                request.birthdate(),
                new Owner(request.owner().name(), request.owner().contact())
        );
    }

    public PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getSex(),
                pet.getBirthdate(),
                new PetResponse.OwnerResponse(
                        pet.getOwner().name(),
                        pet.getOwner().contact()
                ),
                pet.getCreatedAt(),
                pet.getUpdatedAt()
        );
    }

    public void updateEntity(Pet pet, CreatePetRequest request) {
        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setSex(request.sex());
        pet.setBirthdate(request.birthdate());
        pet.setOwner(new Owner(request.owner().name(), request.owner().contact()));
    }
}
