package com.reserve.lab.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.dto.ResponseDto;
import com.reserve.lab.api.service.ReservationService;
import com.reserve.lab.api.service.SemesterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Reservas", description = "Gestión de reservas")
@RestController
@AllArgsConstructor
@Slf4j
public class ReservationController {
    private ReservationService service;
    private SemesterService semesterService;

    @PostMapping(value = "/upload-reservations")
    public ResponseEntity<ResponseDto> uploadReservations(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<ReservationDto> reservations = Arrays.asList(objectMapper.readValue(jsonData, ReservationDto[].class));
            log.info("Reservation list received with {} entries", reservations.size());

            Semester semester = semesterService.findActiveSemester();
            service.deleteAllBySemester(semester);
            return ResponseEntity.ok(new ResponseDto(service.saveExcel(reservations, semester), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @GetMapping(value = "/reservations")
    public ResponseEntity<ResponseDto> findAllReservations(@RequestParam Long semesterId) {
        try {
            return ResponseEntity.ok(new ResponseDto(service.findAllBySemester(semesterId), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @GetMapping(value = "/existing-reservations")
    public ResponseEntity<ResponseDto> findAnyReservation(@RequestParam Long semesterId) {
        try {
            return ResponseEntity.ok(new ResponseDto(!service.findAllBySemester(semesterId).isEmpty(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }
}
