package com.reserve.lab.api.transformer;

import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.SemesterDto;
import org.springframework.stereotype.Component;

@Component
public class SemesterTransformer {
    public Semester createModelFromDto(SemesterDto dto) {
        Semester model = new Semester();
        updateModelFromDto(dto, model);
        return model;
    }

    public void updateModelFromDto(SemesterDto dto, Semester model) {
        model.setStartYear(dto.getStartYear());
        model.setEndYear(dto.getEndYear());
        model.setPeriod(dto.getPeriod());
    }
}
