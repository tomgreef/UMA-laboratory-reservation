package com.reserve.lab.api.model;

import com.reserve.lab.api.model.dto.ReservationDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Responsible {

    public Responsible(ReservationDto dto) {
        this.name = dto.getResponsibleName();
        this.phone = dto.getResponsiblePhone();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del profesor no puede estar vacío")
    private String name;

    private String phone;

    public boolean isInDto(ReservationDto dto) {
        return dto.getResponsibleName().equals(name) && dto.getResponsiblePhone().equals(phone);
    }
}
