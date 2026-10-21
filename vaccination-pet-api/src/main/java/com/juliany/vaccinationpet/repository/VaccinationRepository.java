package com.juliany.vaccinationpet.repository;

import com.juliany.vaccinationpet.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {

    List<Vaccination> findAllByPetIdOrderByIdAsc(Long petId);
}
