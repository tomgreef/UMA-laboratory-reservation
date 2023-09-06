package com.reserve.lab.api.model;

import com.reserve.lab.api.model.type.DayOfWeekType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReservationConflict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id1", referencedColumnName = "id")
    private Reservation reservation1;
    @ManyToOne
    @JoinColumn(name = "reservation_id2", referencedColumnName = "id")
    private Reservation reservation2;
    private LocalDate startDate;
    private LocalDate endDate;
    private DayOfWeekType day;
    private LocalTime startTime;
    private LocalTime endTime;
    @ManyToOne
    private Laboratory laboratory;
    @ManyToOne
    private Semester semester;
}
