package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.Task;
import com.reserve.lab.api.model.dto.AssignmentDto;
import com.reserve.lab.api.repository.ReservationAssignmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationAssignmentService {
    private final ReservationAssignmentRepository repository;
    private final ReservationConflictService conflictService;
    private final TaskService taskService;

    public void saveSolution(List<ReservationAssignment> assignments) {
        repository.saveAll(assignments);
    }

    public void deleteAllBySemester(Semester semester) {
        repository.deleteAllBySemester(semester);
    }

    public AssignmentDto findAssignationBySemester(Long semesterId) {
        AssignmentDto assignmentDto = new AssignmentDto();

        List<Task> taskOfSemester = taskService.findAllBySemester(semesterId);

        if (taskOfSemester.isEmpty()) {
            log.info("No task found for semester id {}", semesterId);
            throw new NotFoundException("No se ha encontrado tareas para el cuatrimestre. Por favor, importe un archivo CSV");
        }

        Task task = taskOfSemester.stream().max(Comparator.comparing(Task::getUpdated)).orElseThrow(() -> {
            log.error("No task with max updated date found for semester id {}", semesterId);
            return new NotFoundException("No se ha encontrado un cuatrimestre mas actualizado");
        });

        assignmentDto.setTask(task);
        assignmentDto.setAssignments(repository.findAllBySemester(task.getSemester()));
        assignmentDto.setConflicts(conflictService.findAllBySemester(task.getSemester()));
        return assignmentDto;
    }

    public void deleteAllByLaboratory(Laboratory laboratory) {
        List<ReservationAssignment> reservationAssignments = repository.findAllByLaboratory(laboratory);
        Set<Semester> semesters = reservationAssignments.stream().map(ReservationAssignment::getSemester).collect(Collectors.toSet());
        semesters.forEach(semester -> {
            conflictService.deleteAllBySemester(semester);
            deleteAllBySemester(semester);
        });
    }
}
