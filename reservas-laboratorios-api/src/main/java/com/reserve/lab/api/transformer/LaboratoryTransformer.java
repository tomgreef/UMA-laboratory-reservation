package com.reserve.lab.api.transformer;

import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.model.dto.LaboratoryDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class LaboratoryTransformer {

    public void mapValuesToModel(LaboratoryDto dto, Laboratory model) {
        model.setName(cleanString(dto.getName()));
        model.setCapacity(dto.getCapacity());
        model.setLocation(cleanString(dto.getLocation()));
        model.setOperatingSystem(cleanString(dto.getOperatingSystem()));
        model.setAdditionalEquipment(cleanString(dto.getAdditionalEquipment()));
    }

    public List<LaboratoryDto> mapModelsToDtos(List<Laboratory> laboratories) {
        List<LaboratoryDto> dtos = new ArrayList<>();
        for (Laboratory laboratory : laboratories) {
            LaboratoryDto dto = new LaboratoryDto();
            dto.setId(laboratory.getId());
            dto.setName(laboratory.getName());
            dto.setCapacity(laboratory.getCapacity());
            dto.setLocation(laboratory.getLocation());
            dto.setOperatingSystem(laboratory.getOperatingSystem());
            dto.setAdditionalEquipment(laboratory.getAdditionalEquipment());

            laboratory.getAdjacentLaboratories().forEach(adjacentLaboratory -> dto.getAdjacentLaboratories().add(adjacentLaboratory.getId()));
            dtos.add(dto);
        }
        return dtos;
    }

    private String cleanString(String string) {
        return ReservationTransformer.cleanString(string);
    }
}
