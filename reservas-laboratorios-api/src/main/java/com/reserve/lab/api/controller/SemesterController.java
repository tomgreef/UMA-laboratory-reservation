package com.reserve.lab.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.IdDto;
import com.reserve.lab.api.model.dto.ResponseDto;
import com.reserve.lab.api.model.dto.SemesterDto;
import com.reserve.lab.api.service.SemesterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

@Tag(name = "Cuatrimestres", description = "Gestión de cuatrimestres")
@RestController
@AllArgsConstructor
@Slf4j
public class SemesterController {
    private SemesterService service;

    @GetMapping(value = "/semesters")
    public ResponseEntity<ResponseDto> findAllSemesters() {
        try {
            return ResponseEntity.ok(new ResponseDto(service.findAll(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @GetMapping(value = "/semesters/active")
    public ResponseEntity<ResponseDto> findActiveSemester() {
        try {
            return ResponseEntity.ok(new ResponseDto(service.findActiveSemester(), null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @PostMapping(value = "/semesters")
    public ResponseEntity<ResponseDto> saveSemester(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SemesterDto semester = objectMapper.readValue(jsonData, SemesterDto.class);
            log.info("Creating new semester: {}", semester);

            Semester model = service.save(semester);
            service.setActiveSemester(model);
            return ResponseEntity.ok(new ResponseDto(model, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @DeleteMapping(value = "/semesters")
    public ResponseEntity<ResponseDto> deleteSemester(@RequestBody Long id) {
        try {
            service.delete(id);
            return findAllSemesters();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @PostMapping(value = "/semesters/active")
    public ResponseEntity<ResponseDto> updateActiveSemester(@RequestBody String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Long semesterId = objectMapper.readValue(jsonData, IdDto.class).getId();
            log.info("Setting semester {} as active", semesterId);
            return ResponseEntity.ok(new ResponseDto(service.setActiveSemesterById(semesterId), null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }
}
