package com.reserve.lab.api.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SemesterDto {
    private String id;

    @NotEmpty
    private Integer startYear;
    @NotEmpty
    private Integer endYear;
    @NotEmpty
    private Integer period;
}
