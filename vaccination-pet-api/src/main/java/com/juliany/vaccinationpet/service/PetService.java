package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchPetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.exception.ResourceNotFoundException;
import com.juliany.vaccinationpet.mapper.PetMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    private static final int PAGE_SIZE = 20;

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final RequestValidationService requestValidationService;

    public PetService(
            PetRepository petRepository,
            PetMapper petMapper,
            RequestValidationService requestValidationService
    ) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.requestValidationService = requestValidationService;
    }

    @Transactional
    public PetResponse create(CreatePetRequest request) {
        Pet pet = petMapper.toEntity(request);
        Pet savedPet = petRepository.saveAndFlush(pet);
        return petMapper.toResponse(savedPet);
    }

    @Transactional(readOnly = true)
    public PageResponse<PetResponse> findAll(int pageNumber) {
        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                PAGE_SIZE,
                Sort.by(Sort.Direction.ASC, "id")
        );
        Page<PetResponse> pets = petRepository.findAll(pageable)
                .map(petMapper::toResponse);
        return PageResponse.from(pets);
    }

    @Transactional(readOnly = true)
    public PetResponse findById(Long id) {
        return petMapper.toResponse(findPetById(id));
    }

    @Transactional
    public PetResponse update(Long id, PatchPetRequest patch) {
        Pet pet = findPetById(id);
        PatchPetRequest.PatchOwnerRequest ownerPatch = patch.getOwner();
        CreatePetRequest.OwnerRequest owner = !patch.isOwnerProvided()
                ? new CreatePetRequest.OwnerRequest(pet.getOwner().name(), pet.getOwner().contact())
                : ownerPatch == null
                        ? null
                        : new CreatePetRequest.OwnerRequest(
                                ownerPatch.isNameProvided() ? ownerPatch.getName() : pet.getOwner().name(),
                                ownerPatch.isContactProvided() ? ownerPatch.getContact() : pet.getOwner().contact()
                        );

        CreatePetRequest mergedRequest = new CreatePetRequest(
                patch.isNameProvided() ? patch.getName() : pet.getName(),
                patch.isSpeciesProvided() ? patch.getSpecies() : pet.getSpecies(),
                patch.isSexProvided() ? patch.getSex() : pet.getSex(),
                patch.isBirthdateProvided() ? patch.getBirthdate() : pet.getBirthdate(),
                owner
        );
        requestValidationService.validate(mergedRequest);
        petMapper.updateEntity(pet, mergedRequest);
        Pet updatedPet = petRepository.saveAndFlush(pet);
        return petMapper.toResponse(updatedPet);
    }

    @Transactional
    public void delete(Long id) {
        Pet pet = findPetById(id);
        petRepository.delete(pet);
        petRepository.flush();
    }

    private Pet findPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pet not found with id: " + id
                ));
    }
}
