package com.reserve.lab.api.model;

import com.reserve.lab.api.model.dto.ReservationDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Professor {
    public Professor(ReservationDto dto) {
        this.name = dto.getProfessorName();
        this.email = dto.getProfessorEmail();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del profesor no puede estar vacío")
    private String name;

    @Email(message = "El email del profesor debe ser válido")
    private String email;

    public boolean isInDto(ReservationDto dto) {
        return dto.getProfessorName().equals(name) && dto.getProfessorEmail().equals(email);
    }
}
