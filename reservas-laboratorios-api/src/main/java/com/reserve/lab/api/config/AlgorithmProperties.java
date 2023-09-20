package com.reserve.lab.api.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "algorithm")
public class AlgorithmProperties {
    @Min(value = 1, message = "El valor debe ser mayor que 0")
    private int populationSize = 100;
    @Min(value = 1, message = "El valor debe ser mayor que 0")
    private int maxGeneration = 100;
    @Min(value = 0, message = "El valor debe estar entre 0 y 1")
    @Max(value = 1, message = "El valor debe estar entre 0 y 1")
    private double elitismSelectionRate = 0.5;
    @Min(value = 0, message = "El valor debe estar entre 0 y 1")
    @Max(value = 1, message = "El valor debe estar entre 0 y 1")
    private double crossoverRate = 0.5;
    @Min(value = 0, message = "El valor debe estar entre 0 y 1")
    @Max(value = 1, message = "El valor debe estar entre 0 y 1")
    private double mutationRate = 0.4;
    @Min(value = 0, message = "El valor debe estar entre 0 y 1")
    @Max(value = 1, message = "El valor debe estar entre 0 y 1")
    private double mutationRepairRate = 0.8;

    public void updateProperties(AlgorithmProperties updatedProperties) {
        this.populationSize = updatedProperties.populationSize;
        this.maxGeneration = updatedProperties.maxGeneration;
        this.crossoverRate = updatedProperties.crossoverRate;
        this.mutationRate = updatedProperties.mutationRate;
        this.mutationRepairRate = updatedProperties.mutationRepairRate;
        this.elitismSelectionRate = updatedProperties.elitismSelectionRate;
    }
}
