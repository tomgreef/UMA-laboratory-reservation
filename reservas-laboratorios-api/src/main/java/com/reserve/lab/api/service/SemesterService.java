package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.SemesterDto;
import com.reserve.lab.api.repository.SemesterRepository;
import com.reserve.lab.api.transformer.SemesterTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SemesterService {
    private SemesterRepository repository;
    private SemesterTransformer transformer;
    private ReservationService reservationService;
    private TaskService taskService;

    public Semester save(SemesterDto semester) {
        return repository.save(transformer.createModelFromDto(semester));
    }

    public List<Semester> findAll() {
        return repository.findAllByOrderByStartYearDesc();
    }

    public Semester findActiveSemester() {
        return repository.findByActiveTrue().orElseThrow(() -> {
            log.info("No active semester found");
            return new NotFoundException("No active semester found");
        });
    }

    public void setActiveSemester(Semester model) {
        List<Semester> activeSemesters = repository.findAllByActiveTrue();
        if (!activeSemesters.isEmpty()) {
            activeSemesters.forEach(semester -> {
                semester.setActive(false);
                repository.save(semester);
            });
        }

        model.setActive(true);
        repository.save(model);
    }

    public Semester setActiveSemesterById(Long id) {
        Semester semester = repository.findById(id).orElseThrow(() -> {
            log.error("No semester found with id: {}", id);
            return new NotFoundException("No semester found with id: " + id);
        });

        setActiveSemester(semester);
        return semester;
    }

    @Transactional
    public void delete(Long id) {
        Semester semester = repository.findById(id).orElseThrow(() -> {
            log.error("No semester found with id: {}", id);
            return new NotFoundException("No semester found with id: " + id);
        });
        reservationService.deleteAllBySemester(semester);
        taskService.deleteAllBySemester(semester);
        repository.deleteById(id);
    }
}
