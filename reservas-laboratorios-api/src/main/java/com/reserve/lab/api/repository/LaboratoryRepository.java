package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Laboratory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryRepository extends CrudRepository<Laboratory, Long> {
    List<Laboratory> findAllByOrderByName();
}
