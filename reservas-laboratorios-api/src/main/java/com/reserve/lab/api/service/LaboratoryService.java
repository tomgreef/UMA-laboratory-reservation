package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.model.Reservation;
import com.reserve.lab.api.model.dto.LaboratoryDto;
import com.reserve.lab.api.model.type.AnythingType;
import com.reserve.lab.api.repository.LaboratoryRepository;
import com.reserve.lab.api.transformer.LaboratoryTransformer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class LaboratoryService {
    private final LaboratoryRepository repository;
    private final LaboratoryTransformer transformer;
    private final AdjacentLaboratoryService adjacentLaboratoryService;
    private final ReservationAssignmentService reservationAssignmentService;

    public List<Laboratory> findAvailableLaboratories(Reservation reservation) {
        Integer capacity = reservation.getStudentsNumber();
        String location = reservation.getLocation();
        String operatingSystem = reservation.getOperatingSystem();
        String additionalEquipment = reservation.getAdditionalEquipment();

        List<Laboratory> result = repository.findAll(buildQuery(capacity, location, operatingSystem, additionalEquipment));
        if (result.isEmpty()) {
            result = repository.findAll(buildQuery(capacity, location, operatingSystem, null));

            if (result.isEmpty()) {
                result = repository.findAll(buildQuery(capacity, location, null, null));

                if (result.isEmpty()) {
                    return repository.findAll(buildQuery(capacity, null, null, null));
                }
            }
        }

        return result;
    }

    private Specification<Laboratory> buildQuery(Integer capacity, String location, String operatingSystem, String additionalEquipment) {
        return (Root<Laboratory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), capacity));

            if (location != null && !location.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("location"), location));
            }

            if (operatingSystem != null && !operatingSystem.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("operatingSystem"), operatingSystem));
            }

            if (additionalEquipment != null && !additionalEquipment.isEmpty() && !additionalEquipment.equals(AnythingType.NOTHING.name())) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("additionalEquipment"), "%" + additionalEquipment + "%"));
            }

            return predicate;
        };
    }

    public List<LaboratoryDto> getAllDto() {
        List<Laboratory> laboratories = repository.findAllByOrderByName();
        return transformer.mapModelsToDtos(laboratories);
    }

    public List<Laboratory> findAll() {
        return repository.findAllByOrderByName();
    }

    public void delete(Long id) {
        Laboratory laboratory = repository.findById(id).orElseThrow(() -> {
            log.error("Laboratory not found");
            throw new NotFoundException("Laboratorio no encontrado");
        });
        reservationAssignmentService.deleteAllByLaboratory(laboratory);
        adjacentLaboratoryService.deleteByLaboratory(laboratory);

        repository.delete(laboratory);
    }

    @Transactional
    public Laboratory upsert(LaboratoryDto dto) {
        Laboratory model = repository.findById(dto.getId()).orElse(new Laboratory());
        transformer.mapValuesToModel(dto, model);
        model = repository.save(model);
        adjacentLaboratoryService.addAdjacentLaboratories(model, dto.getAdjacentLaboratories());

        return model;
    }

    public void upsert(List<Reservation> reservationsWithLaboratoryPreference) {
        List<Laboratory> existingLaboratories = (List<Laboratory>) repository.findAll();
        List<Laboratory> newLaboratoriesToSave = new ArrayList<>();

        for (Reservation reservation : reservationsWithLaboratoryPreference) {
            for (String reservationLaboratoryName : reservation.getLaboratoryPreference()) {
                Optional<Laboratory> foundLaboratory = existingLaboratories.stream().filter(laboratory -> laboratory.getName().equals(reservationLaboratoryName)).findFirst();
                if (foundLaboratory.isEmpty()) {
                    Laboratory newLaboratory = new Laboratory(reservationLaboratoryName, reservation.getStudentsNumber(), reservation.getLocation(), reservation.getOperatingSystem(), reservation.getAdditionalEquipment());
                    newLaboratoriesToSave.add(newLaboratory);
                    existingLaboratories.add(newLaboratory);
                } else if (newLaboratoriesToSave.contains(foundLaboratory.get())) {
                    Laboratory newLaboratory = foundLaboratory.get();
                    if (newLaboratory.getCapacity() < reservation.getStudentsNumber()) {
                        newLaboratory.setCapacity(reservation.getStudentsNumber());
                    }
                }
            }
        }

        try {
            repository.saveAll(newLaboratoriesToSave);
        } catch (Exception e) {
            log.error("Error saving new laboratories for reservations import");
            throw new IllegalArgumentException("Error al guardar los nuevos laboratorios para la importación de reservas. Comprueba que las reservas con preferencia de laboratorio tengan un nombre de laboratorio válido, número de estudiantes y ubicación.");
        }
    }
}
