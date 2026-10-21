package com.juliany.vaccinationpet.repository;

import com.juliany.vaccinationpet.entity.Vaccination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {

    Page<Vaccination> findAllByPetId(Long petId, Pageable pageable);
}
