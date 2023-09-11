package com.reserve.lab.api.service.algorithm;

import com.reserve.lab.api.config.AlgorithmProperties;
import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.model.Reservation;
import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.helper.Solution;
import com.reserve.lab.api.service.LaboratoryService;
import com.reserve.lab.api.service.ReservationAssignmentService;
import com.reserve.lab.api.service.ReservationConflictService;
import com.reserve.lab.api.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class GreedyAlgorithmService {
    private final ReservationService reservationService;
    private final LaboratoryService laboratoryService;
    private final ReservationAssignmentService reservationAssignmentService;
    private final ReservationConflictService reservationConflictService;
    private final GeneticAlgorithmService geneticAlgorithmService;
    private static double lowestPenalty = Integer.MAX_VALUE;
    private static List<ReservationAssignment> bestAssignment;


    public void runAlgorithm(Semester semester) {
        log.info("Running task for semester {} - {} | {}", semester.getStartYear(), semester.getEndYear(), semester.getPeriod());
        long algorithmStartTime = System.nanoTime();
        // Step 0: Remove any previous assignments to this semester
        reservationAssignmentService.deleteAllBySemester(semester);
        reservationConflictService.deleteAllBySemester(semester);

        // Step 1: Get data
        List<Reservation> reservations = reservationService.findAllBySemester(semester);
        List<Laboratory> laboratories = laboratoryService.findAll();

        List<ReservationAssignment> currentAssignment = new ArrayList<>();
        assignReservations(reservations, laboratories, currentAssignment, 0);

        log.info("Best Assignment: " + bestAssignment);
        log.info("Lowest Penalty: " + lowestPenalty);

        new Solution(bestAssignment, geneticAlgorithmService.getPenaltyOccurrences(bestAssignment)).printPenalties();
        log.info("Algorithm finished in {} seconds", geneticAlgorithmService.calculateTimeInSeconds(algorithmStartTime, System.nanoTime()));
    }

    private void assignReservations(List<Reservation> reservations, List<Laboratory> laboratories,
                                    List<ReservationAssignment> currentAssignment, int reservationIndex) {
        if (reservationIndex == reservations.size()) {
            // All reservations have been assigned, calculate penalty.
            double penalty = Solution.getPenaltyScore(geneticAlgorithmService.getPenaltyOccurrences(currentAssignment));
            log.info("Current Assignment {}: {}", penalty, currentAssignment);
            if (penalty < lowestPenalty) {
                lowestPenalty = penalty;
                bestAssignment = new ArrayList<>(currentAssignment);
                log.info("Best Assignment: " + bestAssignment);
                log.info("Lowest Penalty: " + lowestPenalty + "\n");
            }
            return;
        }

        for (int labIndex = 0; labIndex < laboratories.size(); labIndex++) {
            // Try assigning the current reservation to a laboratory.
            currentAssignment.add(new ReservationAssignment(reservations.get(reservationIndex), laboratories.get(labIndex)));
            assignReservations(reservations, laboratories, currentAssignment, reservationIndex + 1);
            currentAssignment.remove(currentAssignment.size() - 1);
        }
    }
}
