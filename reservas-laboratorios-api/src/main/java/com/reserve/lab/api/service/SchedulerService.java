package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Task;
import com.reserve.lab.api.model.type.TaskStatusType;
import com.reserve.lab.api.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Profile("!test")
public class SchedulerService {
    private static final int TIMEOUT_MINUTES_RUNNING_TASK = 30;
    private final TaskRepository repository;
    private final GeneticAlgorithmService geneticAlgorithmService;

    @Scheduled(cron = "${scheduler.cron}")
    public void scheduleTask() {
        log.info("Running task scheduler");
        checkForQueuedTasks();
        checkForOutdatedTasks();
    }

    private void checkForQueuedTasks() {
        List<Task> tasks = repository.findAllByStatus(String.valueOf(TaskStatusType.QUEUED));
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                task.setStatus(String.valueOf(TaskStatusType.RUNNING));
                repository.save(task);
                try {
                    geneticAlgorithmService.runAlgorithm(task.getSemester());
                    // TODO: Future Lines of Research - Try out Exhaustive algorithms
                } catch (Exception e) {
                    log.error("Error running algorithm for semester {} - {} | {}", task.getSemester().getStartYear(), task.getSemester().getEndYear(), task.getSemester().getPeriod());

                    task.setStatus(String.valueOf(TaskStatusType.ERROR));
                    task.setErrorMessages(e.getMessage());
                    repository.save(task);
                    return;
                }
                task.setStatus(String.valueOf(TaskStatusType.COMPLETED));
                repository.save(task);
            }
        }
    }

    private void checkForOutdatedTasks() {
        List<Task> tasks = repository.findAllByStatus(String.valueOf(TaskStatusType.RUNNING));

        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                if (task.getUpdated().plusMinutes(TIMEOUT_MINUTES_RUNNING_TASK).isBefore(LocalDateTime.now())) {
                    task.setStatus(String.valueOf(TaskStatusType.OUTDATED));
                    repository.save(task);
                }
            }
        }
    }
}
