package com.reserve.lab.api.service;

import com.reserve.lab.api.model.*;
import com.reserve.lab.api.model.helper.DateSlot;
import com.reserve.lab.api.model.helper.TimeSlot;
import com.reserve.lab.api.repository.ReservationConflictRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationConflictService {
    private final ReservationConflictRepository repository;

    public void findAndSaveConflicts(List<ReservationAssignment> assignments) {
        repository.saveAll(findConflicts(assignments));
    }

    public List<ReservationConflict> findConflicts(List<ReservationAssignment> assignments) {
        List<ReservationConflict> conflicts = new ArrayList<>();

        if (assignments.size() < 2) return conflicts;

        for (int i = 0; i < assignments.size(); i++) {
            for (int j = i + 1; j < assignments.size(); j++) {
                ReservationAssignment first = assignments.get(i);
                ReservationAssignment second = assignments.get(j);

                if (first.getLaboratory().getId().equals(second.getLaboratory().getId())) {
                    Reservation firstReservation = first.getReservation();
                    Reservation secondReservation = second.getReservation();

                    if (firstReservation.getDay().equals(secondReservation.getDay()) && isReservationOverlapping(firstReservation, secondReservation)) {
                        conflicts.add(createConflict(firstReservation, secondReservation, first.getLaboratory(), first.getSemester()));
                    }
                }
            }
        }

        return conflicts;
    }

    public boolean isReservationOverlapping(Reservation firstReservation, Reservation secondReservation) {
        DateSlot firstDateSlot = new DateSlot(firstReservation.getStartDate(), firstReservation.getEndDate());
        DateSlot secondDateSlot = new DateSlot(secondReservation.getStartDate(), secondReservation.getEndDate());

        if (firstDateSlot.isOverlapping(secondDateSlot)) {
            TimeSlot firstTimeSlot = new TimeSlot(firstReservation.getStartTime(), firstReservation.getEndTime());
            TimeSlot secondTimeSlot = new TimeSlot(secondReservation.getStartTime(), secondReservation.getEndTime());
            return firstTimeSlot.isOverlapping(secondTimeSlot);
        }

        return false;
    }

    private ReservationConflict createConflict(Reservation firstReservation, Reservation secondReservation, Laboratory laboratory, Semester semester) {
        ReservationConflict conflict = new ReservationConflict();

        conflict.setReservation1(firstReservation);
        conflict.setReservation2(secondReservation);
        conflict.setLaboratory(laboratory);
        conflict.setSemester(semester);
        conflict.setDay(firstReservation.getDayType());

        conflict.setStartDate(firstReservation.getStartDate().isBefore(secondReservation.getStartDate()) ? secondReservation.getStartDate() : firstReservation.getStartDate());
        conflict.setEndDate(firstReservation.getEndDate().isAfter(secondReservation.getEndDate()) ? firstReservation.getEndDate() : secondReservation.getEndDate());
        conflict.setStartTime(firstReservation.getStartTime().isBefore(secondReservation.getStartTime()) ? secondReservation.getStartTime() : firstReservation.getStartTime());
        conflict.setEndTime(firstReservation.getEndTime().isAfter(secondReservation.getEndTime()) ? firstReservation.getEndTime() : secondReservation.getEndTime());

        return conflict;
    }

    public List<ReservationConflict> findAllBySemester(Semester semester) {
        return repository.findAllBySemester(semester);
    }

    @Transactional
    public void deleteAllBySemester(Semester semester) {
        repository.deleteAll(findAllBySemester(semester));
    }
}
