package com.reserve.lab.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
public class AdjacentLaboratory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;
    @ManyToOne
    @Getter
    @JoinColumn(name = "adjacent_laboratory_id")
    private Laboratory adjacentLaboratory;
}
