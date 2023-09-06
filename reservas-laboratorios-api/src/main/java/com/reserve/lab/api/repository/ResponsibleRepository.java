package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Responsible;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsibleRepository extends CrudRepository<Responsible, Long> {
    Optional<Responsible> findByName(String name);
}
