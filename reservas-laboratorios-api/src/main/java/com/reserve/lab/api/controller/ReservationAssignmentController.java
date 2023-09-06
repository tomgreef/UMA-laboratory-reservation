package com.reserve.lab.api.controller;

import com.reserve.lab.api.model.dto.ResponseDto;
import com.reserve.lab.api.service.ReservationAssignmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

@Tag(name = "Asignaciones", description = "Gestión de asignaciones API")
@RestController
@AllArgsConstructor
@Slf4j
public class ReservationAssignmentController {
    private final ReservationAssignmentService service;

    @GetMapping(value = "/assignations")
    public ResponseEntity<ResponseDto> findAssignationBySemester(@RequestParam Long semesterId) {
        try {
            log.info("Finding reservation assignations by semester id: {}", semesterId);
            return ResponseEntity.ok(new ResponseDto(service.findAssignationBySemester(semesterId), null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }
}
