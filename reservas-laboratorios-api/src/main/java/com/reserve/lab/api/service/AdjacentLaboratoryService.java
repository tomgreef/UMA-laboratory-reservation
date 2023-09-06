package com.reserve.lab.api.service;

import com.reserve.lab.api.model.AdjacentLaboratory;
import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.repository.AdjacentLaboratoryRepository;
import com.reserve.lab.api.repository.LaboratoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AdjacentLaboratoryService {
    private final AdjacentLaboratoryRepository repository;
    private final LaboratoryRepository laboratoryRepository;

    @Transactional
    public void addAdjacentLaboratories(Laboratory laboratory, List<Long> relatedLaboratories) {
        List<AdjacentLaboratory> currentAdjacentLaboratories = repository.findAllAdjacentLaboratoriesByLaboratoryId(laboratory.getId());

        // Delete the ones that are not in the list of the dto and the related laboratory relation
        currentAdjacentLaboratories.forEach(currentAdjacentLaboratory -> {
            if (!relatedLaboratories.contains(currentAdjacentLaboratory.getAdjacentLaboratory().getId())) {
                AdjacentLaboratory relatedLaboratoryModel = getAdjacentLaboratory(currentAdjacentLaboratory.getAdjacentLaboratory(), laboratory);

                repository.delete(currentAdjacentLaboratory);
                repository.delete(relatedLaboratoryModel);
            }
        });

        // Add the new ones and save the relation for the other laboratory
        relatedLaboratories.forEach(relatedLaboratoryId -> {
            boolean doesNotExistInCurrentModels = currentAdjacentLaboratories.stream().noneMatch(currentAdjacentLaboratory -> currentAdjacentLaboratory.getAdjacentLaboratory().getId().equals(relatedLaboratoryId));
            if (doesNotExistInCurrentModels) {
                Laboratory relatedLaboratory = laboratoryRepository.findById(relatedLaboratoryId).orElseThrow(() -> new RuntimeException("Laboratory not found, id: " + relatedLaboratoryId));
                AdjacentLaboratory model = new AdjacentLaboratory();
                AdjacentLaboratory relatedLaboratoryModel = new AdjacentLaboratory();

                model.setLaboratory(laboratory);
                model.setAdjacentLaboratory(relatedLaboratory);

                relatedLaboratoryModel.setLaboratory(relatedLaboratory);
                relatedLaboratoryModel.setAdjacentLaboratory(laboratory);

                repository.save(model);
                repository.save(relatedLaboratoryModel);
            }
        });
    }

    private AdjacentLaboratory getAdjacentLaboratory(Laboratory laboratory, Laboratory adjacentLaboratory) {
        return repository.findByLaboratoryAndAdjacentLaboratory(laboratory, adjacentLaboratory).orElseThrow(() -> new RuntimeException("Laboratory not found, laboratoryId: " + laboratory.getId() + ", adjacentLaboratoryId: " + adjacentLaboratory.getId()));
    }

    public void deleteByLaboratory(Laboratory laboratory) {
        repository.deleteAllByLaboratoryOrAdjacentLaboratory(laboratory);
    }
}
