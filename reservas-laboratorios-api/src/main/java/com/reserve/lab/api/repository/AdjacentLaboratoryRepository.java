package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.AdjacentLaboratory;
import com.reserve.lab.api.model.Laboratory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdjacentLaboratoryRepository extends CrudRepository<AdjacentLaboratory, Long>, JpaSpecificationExecutor<Laboratory> {
    List<AdjacentLaboratory> findAllAdjacentLaboratoriesByLaboratoryId(Long laboratoryId);

    Optional<AdjacentLaboratory> findByLaboratoryAndAdjacentLaboratory(Laboratory laboratory, Laboratory adjacentLaboratory);

    @Modifying
    @Query("DELETE FROM AdjacentLaboratory al WHERE al.laboratory = :laboratory OR al.adjacentLaboratory = :laboratory")
    void deleteAllByLaboratoryOrAdjacentLaboratory(Laboratory laboratory);
}
