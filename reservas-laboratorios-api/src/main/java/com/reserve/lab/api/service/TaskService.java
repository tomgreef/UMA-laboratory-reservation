package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.Task;
import com.reserve.lab.api.model.type.TaskStatusType;
import com.reserve.lab.api.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository repository;

    @Transactional
    public void createTask(Semester semester) {
        Optional<Task> foundTask = repository.findByStatusAndSemester(String.valueOf(TaskStatusType.QUEUED), semester);
        if (foundTask.isEmpty()) {
            Task task = repository.save(new Task(semester, String.valueOf(TaskStatusType.QUEUED)));
            log.info("Created task for semester {} - {} | {}", task.getSemester().getStartYear(), task.getSemester().getEndYear(), task.getSemester().getPeriod());
        }
    }

    public List<Task> findAllBySemester(Long id) {
        return repository.findAllBySemesterId(id);
    }

    public void deleteAllBySemester(Semester semester) {
        repository.deleteAll(repository.findAllBySemesterId(semester.getId()));
    }
}
