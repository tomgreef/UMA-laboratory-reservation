package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Semester;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends CrudRepository<Semester, Long> {
    List<Semester> findAllByActiveTrue();

    Optional<Semester> findByActiveTrue();

    List<Semester> findAllByOrderByStartYearDesc();
}
