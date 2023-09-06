package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {
    Optional<Department> findByName(String name);
}
