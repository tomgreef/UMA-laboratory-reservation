package com.reserve.lab.api.reservation;

import com.reserve.lab.api.config.EnableTestContainer;
import com.reserve.lab.api.config.TestContainerInitializer;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.dto.ReservationDtoWithError;
import com.reserve.lab.api.model.dto.SemesterDto;
import com.reserve.lab.api.service.ReservationService;
import com.reserve.lab.api.service.SemesterService;
import com.reserve.lab.api.utils.DataGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableTestContainer
@ContextConfiguration(initializers = {TestContainerInitializer.class})
@ActiveProfiles("test")
@Slf4j
class FileImportTests {
    private final int NUMBER_OF_RESERVATIONS = 100;
    @Autowired
    private ReservationService service;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private DataGeneratorService dataGeneratorService;

    @Test
    void importFileWithNoErrors() {
        List<ReservationDto> reservations = dataGeneratorService.getReservations(NUMBER_OF_RESERVATIONS);
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();

        Semester semester = semesterService.save(semesterDto);
        List<ReservationDtoWithError> reservationWithErrors = service.saveExcel(reservations, semester);

        assertTrue(reservationWithErrors.isEmpty());
    }

    @Test
    void importFileWithErrors() {
        List<ReservationDto> reservations = dataGeneratorService.getReservations(NUMBER_OF_RESERVATIONS);
        Random random = new Random();
        int randomIndex = random.nextInt(NUMBER_OF_RESERVATIONS);
        reservations.get(randomIndex).setEndDate(null);
        SemesterDto semesterDto = dataGeneratorService.getSemesterDto();

        Semester semester = semesterService.save(semesterDto);
        List<ReservationDtoWithError> reservationWithErrors = service.saveExcel(reservations, semester);

        assertFalse(reservationWithErrors.isEmpty());
        assertNull(reservationWithErrors.get(0).getDto().getEndDate());
        assertEquals((int) reservationWithErrors.get(0).getRowNumber(), randomIndex + ReservationService.ROW_NUMBER_OFFSET);
    }
}
