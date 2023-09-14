package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.model.Reservation;
import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.helper.Solution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BranchAndBoundAlgorithmService {
    private final GeneticAlgorithmService geneticAlgorithmService;
    private final ReservationService reservationService;
    private final LaboratoryService laboratoryService;
    private final ReservationAssignmentService reservationAssignmentService;
    private final ReservationConflictService reservationConflictService;
    private static double bestSolutionPenalty = Double.POSITIVE_INFINITY;
    private static Solution bestSolution;
    private static int counter;

    public void runAlgorithm(Semester semester) {
        log.info("Running task for semester {} - {} | {}", semester.getStartYear(), semester.getEndYear(), semester.getPeriod());
        long algorithmStartTime = System.nanoTime();
        // Step 0: Remove any previous assignments to this semester
        reservationAssignmentService.deleteAllBySemester(semester);
        reservationConflictService.deleteAllBySemester(semester);

        List<Reservation> reservations = reservationService.findAllBySemester(semester);
        List<Laboratory> laboratories = laboratoryService.findAll();

        int numReservations = reservations.size();
        int numLaboratories = laboratories.size();

        bestSolution = new Solution();
        counter = 0;

        branchAndBound(0, numReservations, numLaboratories, reservations, laboratories, new Solution());

        log.info("Best Solution Penalty Score: " + bestSolution.getPenaltyScore());
        bestSolution.printPenalties();
        geneticAlgorithmService.saveSolution(bestSolution, semester);
        log.info("Algorithm finished in {} seconds", geneticAlgorithmService.calculateTimeInSeconds(algorithmStartTime, System.nanoTime()));
    }

    private void branchAndBound(int depth, int numReservations, int numLaboratories,
                                List<Reservation> reservations, List<Laboratory> laboratories, Solution currentSolution) {
        if (depth == numReservations) {
            // Reached a leaf node (complete assignment)
            counter++;
            log.info("Reached a leaf node, counter: {}", counter);
            double currentPenalty = currentSolution.getPenaltyScore();
            if (currentPenalty < bestSolutionPenalty) {
                log.info("Found a better solution {}", currentPenalty);
                bestSolutionPenalty = currentPenalty;
                bestSolution = new Solution();
                bestSolution.setAssignments(new ArrayList<>(currentSolution.getAssignments()));
                bestSolution.setPenalties(new EnumMap<>(currentSolution.getPenalties()));
            }
            return;
        }

        for (int labIndex = 0; labIndex < numLaboratories; labIndex++) {
            Reservation reservation = reservations.get(depth);
            Laboratory laboratory = laboratories.get(labIndex);
            ReservationAssignment assignment = new ReservationAssignment(reservation, laboratory);

            Solution tempSolution = new Solution();
            tempSolution.setAssignments(new ArrayList<>(currentSolution.getAssignments()));
            tempSolution.getAssignments().add(assignment);
            tempSolution.setPenalties(geneticAlgorithmService.getPenaltyOccurrences(tempSolution));
            double assignmentPenalty = tempSolution.getPenaltyScore();

            // Pruning: Skip this assignment if it cannot lead to a better solution
            if (assignmentPenalty >= bestSolutionPenalty) {
                continue;
            }


            currentSolution.getAssignments().add(assignment);
            currentSolution.setPenalties(geneticAlgorithmService.getPenaltyOccurrences(currentSolution));

            branchAndBound(depth + 1, numReservations, numLaboratories, reservations, laboratories, currentSolution);

            currentSolution.getAssignments().remove(assignment);
            currentSolution.setPenalties(geneticAlgorithmService.getPenaltyOccurrences(currentSolution));
        }
    }
}
