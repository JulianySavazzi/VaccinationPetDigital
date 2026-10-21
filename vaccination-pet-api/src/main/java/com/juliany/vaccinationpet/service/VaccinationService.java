package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Vaccination;
import com.juliany.vaccinationpet.exception.ResourceNotFoundException;
import com.juliany.vaccinationpet.mapper.VaccinationMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
import com.juliany.vaccinationpet.repository.VaccinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;
    private final VaccinationMapper vaccinationMapper;

    public VaccinationService(
            VaccinationRepository vaccinationRepository,
            PetRepository petRepository,
            VaccinationMapper vaccinationMapper
    ) {
        this.vaccinationRepository = vaccinationRepository;
        this.petRepository = petRepository;
        this.vaccinationMapper = vaccinationMapper;
    }

    @Transactional
    public VaccinationResponse create(Long petId, CreateVaccinationRequest request) {
        Pet pet = findPetById(petId);
        Vaccination vaccination = vaccinationMapper.toEntity(pet, request);
        Vaccination savedVaccination = vaccinationRepository.saveAndFlush(vaccination);
        return vaccinationMapper.toResponse(savedVaccination);
    }

    @Transactional(readOnly = true)
    public List<VaccinationResponse> findAllByPetId(Long petId) {
        findPetById(petId);
        return vaccinationRepository.findAllByPetIdOrderByIdAsc(petId)
                .stream()
                .map(vaccinationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VaccinationResponse findById(Long id) {
        return vaccinationMapper.toResponse(findVaccinationById(id));
    }

    @Transactional
    public VaccinationResponse update(Long id, CreateVaccinationRequest request) {
        Vaccination vaccination = findVaccinationById(id);
        vaccinationMapper.updateEntity(vaccination, request);
        Vaccination updatedVaccination = vaccinationRepository.saveAndFlush(vaccination);
        return vaccinationMapper.toResponse(updatedVaccination);
    }

    @Transactional
    public void delete(Long id) {
        Vaccination vaccination = findVaccinationById(id);
        vaccinationRepository.delete(vaccination);
        vaccinationRepository.flush();
    }

    private Pet findPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pet not found with id: " + id
                ));
    }

    private Vaccination findVaccinationById(Long id) {
        return vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vaccination not found with id: " + id
                ));
    }
}
