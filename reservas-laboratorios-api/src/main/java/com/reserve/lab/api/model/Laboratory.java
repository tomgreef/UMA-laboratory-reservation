package com.reserve.lab.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Laboratory {
    public Laboratory(String name, Integer capacity, String location, String operatingSystem, String additionalEquipment) {
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.operatingSystem = operatingSystem;
        this.additionalEquipment = additionalEquipment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre del laboratorio es requerido")
    private String name;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "adjacent_laboratory",
            joinColumns = @JoinColumn(name = "laboratory_id"),
            inverseJoinColumns = @JoinColumn(name = "adjacent_laboratory_id")
    )
    @JsonIgnore
    private List<Laboratory> adjacentLaboratories = List.of();
    @NotNull(message = "La capacidad del laboratorio es requerida")
    private Integer capacity;
    @NotBlank(message = "La ubicación del laboratorio es requerida")
    private String location;
    private String operatingSystem;
    private String additionalEquipment;

    public boolean isSuitableFor(Reservation reservation) {
        if (reservation.getAdditionalEquipment() != null && !reservation.getAdditionalEquipment().isEmpty() && (this.additionalEquipment == null || this.additionalEquipment.isEmpty() || !this.additionalEquipment.contains(reservation.getAdditionalEquipment())))
            return false;
        if (reservation.getOperatingSystem() != null && !reservation.getOperatingSystem().isEmpty() && (this.operatingSystem == null || this.operatingSystem.isEmpty() || !this.operatingSystem.equals(reservation.getOperatingSystem())))
            return false;
        if (reservation.getLocation() != null && !reservation.getLocation().isEmpty() && !this.location.equals(reservation.getLocation()))
            return false;

        return this.capacity >= reservation.getStudentsNumber();
    }
}
