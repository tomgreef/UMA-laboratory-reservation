package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Professor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends CrudRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
}
