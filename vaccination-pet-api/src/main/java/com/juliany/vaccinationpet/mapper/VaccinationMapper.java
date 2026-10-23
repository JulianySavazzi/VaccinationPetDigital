package com.juliany.vaccinationpet.mapper;

import com.juliany.vaccinationpet.dto.CreateVaccinationRequest;
import com.juliany.vaccinationpet.dto.VaccinationResponse;
import com.juliany.vaccinationpet.entity.Pet;
import com.juliany.vaccinationpet.entity.Vaccination;
import org.springframework.stereotype.Component;

@Component
public class VaccinationMapper {

    public Vaccination toEntity(Pet pet, CreateVaccinationRequest request) {
        return new Vaccination(
                pet,
                request.vaccineName(),
                request.vaccineBatchNumber(),
                request.applicationDate(),
                request.expirationDate(),
                request.nextVaccinationDate()
        );
    }

    public VaccinationResponse toResponse(Vaccination vaccination) {
        return new VaccinationResponse(
                vaccination.getId(),
                vaccination.getVersion(),
                vaccination.getPet().getId(),
                vaccination.getVaccineName(),
                vaccination.getVaccineBatchNumber(),
                vaccination.getApplicationDate(),
                vaccination.getExpirationDate(),
                vaccination.getNextVaccinationDate(),
                vaccination.getCreatedAt(),
                vaccination.getUpdatedAt()
        );
    }

    public void updateEntity(Vaccination vaccination, CreateVaccinationRequest request) {
        vaccination.setVaccineName(request.vaccineName());
        vaccination.setVaccineBatchNumber(request.vaccineBatchNumber());
        vaccination.setApplicationDate(request.applicationDate());
        vaccination.setExpirationDate(request.expirationDate());
        vaccination.setNextVaccinationDate(request.nextVaccinationDate());
    }
}
