package com.reserve.lab.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReservationAssignment {
    public ReservationAssignment(Reservation reservation, Laboratory laboratory) {
        this.reservation = reservation;
        this.laboratory = laboratory;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Reservation reservation;
    @ManyToOne
    private Laboratory laboratory;

    @ManyToOne
    private Semester semester;
}
