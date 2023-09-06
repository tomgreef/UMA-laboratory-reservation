package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAllByStatus(String status);

    Optional<Task> findByStatusAndSemester(String status, Semester semester);

    List<Task> findAllBySemesterId(Long id);
}
