package com.reserve.lab.api.model.dto;

import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.ReservationConflict;
import com.reserve.lab.api.model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {
    private List<ReservationAssignment> assignments;
    private List<ReservationConflict> conflicts;
    private Task task;
}
