package com.juliany.vaccinationpet.service;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.PageResponse;
import com.juliany.vaccinationpet.dto.PatchVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Vaccination;
import com.juliany.vaccinationpet.exception.ResourceNotFoundException;
import com.juliany.vaccinationpet.mapper.VaccinationMapper;
import com.juliany.vaccinationpet.repository.PetRepository;
import com.juliany.vaccinationpet.repository.VaccinationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VaccinationService {

    private static final int PAGE_SIZE = 20;

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;
    private final VaccinationMapper vaccinationMapper;
    private final RequestValidationService requestValidationService;

    public VaccinationService(
            VaccinationRepository vaccinationRepository,
            PetRepository petRepository,
            VaccinationMapper vaccinationMapper,
            RequestValidationService requestValidationService
    ) {
        this.vaccinationRepository = vaccinationRepository;
        this.petRepository = petRepository;
        this.vaccinationMapper = vaccinationMapper;
        this.requestValidationService = requestValidationService;
    }

    @Transactional
    public VaccinationResponse create(Long petId, CreateVaccinationRequest request) {
        Pet pet = findPetById(petId);
        Vaccination vaccination = vaccinationMapper.toEntity(pet, request);
        Vaccination savedVaccination = vaccinationRepository.saveAndFlush(vaccination);
        return vaccinationMapper.toResponse(savedVaccination);
    }

    @Transactional(readOnly = true)
    public PageResponse<VaccinationResponse> findAllByPetId(Long petId, int pageNumber) {
        findPetById(petId);
        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                PAGE_SIZE,
                Sort.by(Sort.Direction.ASC, "id")
        );
        Page<VaccinationResponse> vaccinations =
                vaccinationRepository.findAllByPetId(petId, pageable)
                        .map(vaccinationMapper::toResponse);
        return PageResponse.from(vaccinations);
    }

    @Transactional(readOnly = true)
    public VaccinationResponse findById(Long id) {
        return vaccinationMapper.toResponse(findVaccinationById(id));
    }

    @Transactional
    public VaccinationResponse update(Long id, PatchVaccinationRequest patch) {
        Vaccination vaccination = findVaccinationById(id);
        CreateVaccinationRequest mergedRequest = new CreateVaccinationRequest(
                patch.isVaccineNameProvided() ? patch.getVaccineName() : vaccination.getVaccineName(),
                patch.isVaccineBatchNumberProvided()
                        ? patch.getVaccineBatchNumber()
                        : vaccination.getVaccineBatchNumber(),
                patch.isApplicationDateProvided()
                        ? patch.getApplicationDate()
                        : vaccination.getApplicationDate(),
                patch.isExpirationDateProvided()
                        ? patch.getExpirationDate()
                        : vaccination.getExpirationDate(),
                patch.isNextVaccinationDateProvided()
                        ? patch.getNextVaccinationDate()
                        : vaccination.getNextVaccinationDate()
        );
        requestValidationService.validate(mergedRequest);
        vaccinationMapper.updateEntity(vaccination, mergedRequest);
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
