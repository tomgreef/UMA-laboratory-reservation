package com.reserve.lab.api.model.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DateSlot {
    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateSlot timeSlot = (DateSlot) o;
        return startDate.equals(timeSlot.startDate) && endDate.equals(timeSlot.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    public boolean isOverlapping(DateSlot dateSlot) {
        return this.startDate.isBefore(dateSlot.endDate) && this.endDate.isAfter(dateSlot.startDate);
    }

    public int getOverlappingWeeks(DateSlot dateSlot) {
        LocalDate start = this.startDate.isBefore(dateSlot.startDate) ? dateSlot.startDate : this.startDate;
        LocalDate end = this.endDate.isAfter(dateSlot.endDate) ? dateSlot.endDate : this.endDate;
        return (int) ChronoUnit.WEEKS.between(start, end);
    }
}
