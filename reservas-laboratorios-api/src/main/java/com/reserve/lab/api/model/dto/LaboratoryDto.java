package com.reserve.lab.api.model.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryDto {
    private Long id;
    private String name;
    private List<Long> adjacentLaboratories = new ArrayList<>();
    private Integer capacity;
    private String location;
    private String operatingSystem;
    private String additionalEquipment;
}
