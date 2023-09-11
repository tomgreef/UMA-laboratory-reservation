package com.reserve.lab.api.assignation;

import com.reserve.lab.api.config.AlgorithmProperties;
import com.reserve.lab.api.config.EnableTestContainer;
import com.reserve.lab.api.config.TestContainerInitializer;
import com.reserve.lab.api.model.Reservation;
import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.ReservationConflict;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.AssignmentDto;
import com.reserve.lab.api.model.dto.LaboratoryDto;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.dto.SemesterDto;
import com.reserve.lab.api.model.helper.Solution;
import com.reserve.lab.api.model.type.PenaltyType;
import com.reserve.lab.api.service.*;
import com.reserve.lab.api.service.algorithm.GeneticAlgorithmService;
import com.reserve.lab.api.utils.DataGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableTestContainer
@ContextConfiguration(initializers = {TestContainerInitializer.class})
@ActiveProfiles("test")
class GeneticAlgorithmTest {
    private final int NUMBER_OF_RESERVATIONS = 100;
    @Autowired
    private GeneticAlgorithmService service;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationAssignmentService reservationAssignmentService;
    @Autowired
    private LaboratoryService laboratoryService;
    @Autowired
    private AlgorithmProperties algorithmProperties;
    @Autowired
    private DataGeneratorService dataGeneratorService;
    @Autowired
    private SemesterService semesterService;

    @Test
    void algorithmRuns() {
        List<ReservationDto> reservations = dataGeneratorService.getReservations(NUMBER_OF_RESERVATIONS);
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();
        List<LaboratoryDto> laboratories = dataGeneratorService.getLaboratories(10);

        Semester semester = semesterService.save(semesterDto);
        reservationService.saveExcel(reservations, semester);
        laboratories.forEach(laboratoryDto -> laboratoryService.upsert(laboratoryDto));
        service.runAlgorithm(semester);
        AssignmentDto assignation = reservationAssignmentService.findAssignationBySemester(semester.getId());

        assertEquals(NUMBER_OF_RESERVATIONS, assignation.getAssignments().size());
        // For each assignment, check if it has a laboratory and the reservations is there once
        Set<Long> ids = new HashSet<>();
        assignation.getAssignments().forEach(assignment -> {
            assertNotNull(assignment.getLaboratory());
            assertFalse(ids.contains(assignment.getReservation().getId()));
            ids.add(assignment.getReservation().getId());
        });
        // For each reservation, check if it is in the assignation
        reservations.forEach(reservationDto -> {
            boolean found = false;
            for (ReservationAssignment assignment : assignation.getAssignments()) {
                if (assignment.getReservation().getPublicId().toString().equals(reservationDto.getPublicId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        });
        // For the conflicts, check if the reservations are different
        assignation.getConflicts().forEach(conflict -> assertNotEquals(conflict.getReservation1().getId(), conflict.getReservation2().getId()));
    }

    @Test
    void reservationGetsMostlyLaboratoryPreference() {
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();
        List<LaboratoryDto> laboratories = dataGeneratorService.getLaboratories(4);
        List<ReservationDto> reservations = dataGeneratorService.getReservations(NUMBER_OF_RESERVATIONS, laboratories);

        Semester semester = semesterService.save(semesterDto);
        reservationService.saveExcel(reservations, semester);
        laboratories.forEach(laboratoryDto -> laboratoryService.upsert(laboratoryDto));
        service.runAlgorithm(semester);
        AssignmentDto assignation = reservationAssignmentService.findAssignationBySemester(semester.getId());

        int count = 0;
        for (ReservationAssignment assignment : assignation.getAssignments()) {
            if (assignment.getReservation().getLaboratoryPreference().contains(assignment.getLaboratory().getName())) {
                count++;
            }
        }
        assertTrue(count > NUMBER_OF_RESERVATIONS * 0.8);
    }

    @Test
    void assignationCreatesCorrectConflicts() {
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();
        List<LaboratoryDto> laboratories = dataGeneratorService.getLaboratories(1);
        List<ReservationDto> reservations = dataGeneratorService.getReservations(1, laboratories);
        reservations.add(reservations.get(0));

        Semester semester = semesterService.save(semesterDto);
        laboratories.forEach(laboratoryDto -> laboratoryService.upsert(laboratoryDto));
        reservationService.saveExcel(reservations, semester);
        service.runAlgorithm(semester);
        AssignmentDto assignation = reservationAssignmentService.findAssignationBySemester(semester.getId());

        assertEquals(1, assignation.getConflicts().size());
        ReservationConflict conflict = assignation.getConflicts().get(0);
        assertEquals(conflict.getReservation1().getDayType(), conflict.getReservation2().getDayType());
        assertEquals(conflict.getReservation1().getDayType(), conflict.getDay());
    }

    @Test
    void algorithmRespectsCapacityConstraints() {
        // Generate reservations and laboratories with known constraints
        List<ReservationDto> reservations = dataGeneratorService.getReservations(NUMBER_OF_RESERVATIONS);
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();
        List<LaboratoryDto> laboratories = dataGeneratorService.getLaboratoriesForReservations(reservations);

        Semester semester = semesterService.save(semesterDto);
        reservationService.saveExcel(reservations, semester);
        laboratories.forEach(laboratoryDto -> laboratoryService.upsert(laboratoryDto));
        service.runAlgorithm(semester);
        AssignmentDto assignation = reservationAssignmentService.findAssignationBySemester(semester.getId());

        // Verify that each assignment respects the capacity constraint
        assignation.getAssignments().forEach(assignment -> assertTrue(assignment.getLaboratory().getCapacity() >= assignment.getReservation().getStudentsNumber()));
        assertEquals(0, assignation.getConflicts().size());
    }

    @Test
    void algorithmOptimizesInitialPopulation() {
        List<ReservationDto> reservationsDto = dataGeneratorService.getReservations(NUMBER_OF_RESERVATIONS);
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();
        List<LaboratoryDto> laboratories = dataGeneratorService.getLaboratories(10);

        Semester semester = semesterService.save(semesterDto);
        reservationService.saveExcel(reservationsDto, semester);
        laboratories.forEach(laboratoryDto -> laboratoryService.upsert(laboratoryDto));

        List<Reservation> reservations = reservationService.findAllBySemester(semester);

        // Calculate the initial penalty score
        Solution solutionWithNoPenaltyScore = service.createRandomSolution(reservations, laboratoryService.findAll());
        solutionWithNoPenaltyScore.setPenalties(service.getPenaltyOccurrences(solutionWithNoPenaltyScore.getAssignments()));
        double initialPenaltyScore = solutionWithNoPenaltyScore.getPenaltyScore();

        service.runAlgorithm(semester);
        AssignmentDto assignation = reservationAssignmentService.findAssignationBySemester(semester.getId());

        // Calculate the optimized penalty score
        Solution solution = new Solution(assignation.getAssignments(), null);
        EnumMap<PenaltyType, Integer> penaltyOccurrences = service.getPenaltyOccurrences(solution.getAssignments());
        solution.setPenalties(penaltyOccurrences);
        double optimizedPenaltyScore = solution.getPenaltyScore();

        // Verify that the optimized objective function is better (or equal) than the initial objective function
        assertTrue(optimizedPenaltyScore <= initialPenaltyScore);
    }

    @Test
    void algorithmExecutesHundredGenerationsOfHundredSolutionsWithHundredReservationsWithinOneMinute() {
        List<LaboratoryDto> laboratories = dataGeneratorService.getLaboratories(10);
        List<ReservationDto> reservations = dataGeneratorService.getReservations(100);
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();

        Semester semester = semesterService.save(semesterDto);
        laboratories.forEach(laboratoryDto -> laboratoryService.upsert(laboratoryDto));
        reservationService.saveExcel(reservations, semester);

        // Measure execution time
        int previousPopulationSize = algorithmProperties.getPopulationSize();
        int previousMaxGeneration = algorithmProperties.getMaxGeneration();
        algorithmProperties.setPopulationSize(100);
        algorithmProperties.setMaxGeneration(100);
        long startTime = System.currentTimeMillis();
        service.runAlgorithm(semester);
        long endTime = System.currentTimeMillis();
        algorithmProperties.setPopulationSize(previousPopulationSize);
        algorithmProperties.setMaxGeneration(previousMaxGeneration);

        long executionTimeInSeconds = (endTime - startTime) / 1000;
        int MAX_ALLOWED_EXECUTION_TIME = 60;
        assertTrue(executionTimeInSeconds < MAX_ALLOWED_EXECUTION_TIME);
    }
}
