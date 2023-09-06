package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long> {
    Optional<Subject> findByNameAndGroupAndSubgroup(String name, String group, String subgroup);
}
