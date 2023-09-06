package com.reserve.lab.api.model.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Objects;

@AllArgsConstructor
@Builder
public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return startTime.equals(timeSlot.startTime) && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    public boolean isOverlapping(TimeSlot timeSlot) {
        return this.startTime.isBefore(timeSlot.endTime) && this.endTime.isAfter(timeSlot.startTime);
    }
}
