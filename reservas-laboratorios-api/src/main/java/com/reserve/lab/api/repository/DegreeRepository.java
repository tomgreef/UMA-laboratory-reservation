package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Degree;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DegreeRepository extends CrudRepository<Degree, Long> {
    Optional<Degree> findByName(String name);
}
