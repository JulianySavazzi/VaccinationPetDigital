package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.dto.CreatePetRequest;
import com.juliany.vaccinationpet.dto.PetResponse;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.exception.ResourceNotFoundException;
import com.juliany.vaccinationpet.mapper.PetMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public PetService(PetRepository petRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
    }

    @Transactional
    public PetResponse create(CreatePetRequest request) {
        Pet pet = petMapper.toEntity(request);
        Pet savedPet = petRepository.saveAndFlush(pet);
        return petMapper.toResponse(savedPet);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> findAll() {
        return petRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(petMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetResponse findById(Long id) {
        return petMapper.toResponse(findPetById(id));
    }

    @Transactional
    public PetResponse update(Long id, CreatePetRequest request) {
        Pet pet = findPetById(id);
        petMapper.updateEntity(pet, request);
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
