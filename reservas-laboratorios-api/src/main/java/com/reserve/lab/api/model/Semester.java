package com.reserve.lab.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "semester",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"start_year", "end_year", "period"})})
@Setter
@Getter
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_year", nullable = false)
    private int startYear;

    @Column(name = "end_year", nullable = false)
    private int endYear;

    @Column(name = "period", nullable = false)
    private int period;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean active;
}
