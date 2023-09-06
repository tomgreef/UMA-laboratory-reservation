package com.reserve.lab.api.controller;

import com.reserve.lab.api.config.AlgorithmProperties;
import com.reserve.lab.api.model.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Algoritmos", description = "Gestión de la configuración del algoritmo")
@RestController
@AllArgsConstructor
@Slf4j
public class AlgorithmPropertiesController {
    private final AlgorithmProperties algorithmProperties;

    @GetMapping("/properties")
    public ResponseEntity<AlgorithmProperties> getProperties() {
        return ResponseEntity.ok(algorithmProperties);
    }

    @PostMapping("/properties")
    public ResponseEntity<ResponseDto> updateProperties(@Valid @RequestBody AlgorithmProperties updatedProperties) {
        try {
            algorithmProperties.updateProperties(updatedProperties);
            return ResponseEntity.ok(new ResponseDto("Algorithm properties updated successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(null, e.getMessage()));
        }
    }
}
