package com.reserve.lab.api.assignation;

import com.reserve.lab.api.config.AlgorithmProperties;
import com.reserve.lab.api.config.EnableTestContainer;
import com.reserve.lab.api.config.TestContainerInitializer;
import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.AssignmentDto;
import com.reserve.lab.api.model.dto.LaboratoryDto;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.dto.SemesterDto;
import com.reserve.lab.api.service.LaboratoryService;
import com.reserve.lab.api.service.ReservationAssignmentService;
import com.reserve.lab.api.service.ReservationService;
import com.reserve.lab.api.service.SemesterService;
import com.reserve.lab.api.service.algorithm.GreedyAlgorithmService;
import com.reserve.lab.api.utils.DataGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableTestContainer
@ContextConfiguration(initializers = {TestContainerInitializer.class})
@ActiveProfiles("test")
class GreedyAlgorithmTest {
    private final int NUMBER_OF_RESERVATIONS = 100;
    @Autowired
    private GreedyAlgorithmService service;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationAssignmentService reservationAssignmentService;
    @Autowired
    private LaboratoryService laboratoryService;
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
}
