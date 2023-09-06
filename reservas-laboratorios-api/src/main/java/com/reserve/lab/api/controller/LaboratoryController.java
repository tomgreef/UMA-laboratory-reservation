package com.reserve.lab.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reserve.lab.api.model.dto.LaboratoryDto;
import com.reserve.lab.api.model.dto.ResponseDto;
import com.reserve.lab.api.service.LaboratoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

@Tag(name = "Laboratorios", description = "Gestión de laboratios")
@RestController
@AllArgsConstructor
@Slf4j
public class LaboratoryController {
    private LaboratoryService service;

    @GetMapping(value = "/laboratories")
    public ResponseEntity<ResponseDto> findAllLaboratories() {
        try {
            return ResponseEntity.ok((new ResponseDto(service.getAllDto(), null)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @PostMapping(value = "/laboratories")
    public ResponseEntity<ResponseDto> upsertLaboratory(@RequestBody String jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LaboratoryDto laboratory = mapper.readValue(jsonData, LaboratoryDto.class);
            return ResponseEntity.ok(new ResponseDto(service.upsert(laboratory), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }

    @DeleteMapping(value = "/laboratories")
    @Transactional
    public ResponseEntity<ResponseDto> deleteLaboratory(@RequestBody Long id) {
        try {
            service.delete(id);
            return findAllLaboratories();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }
}
