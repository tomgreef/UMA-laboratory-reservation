package com.reserve.lab.api.model;

import com.reserve.lab.api.model.dto.ReservationDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Subject {
    public Subject(ReservationDto dto) {
        this.name = dto.getSubjectName();
        this.course = Integer.parseInt(dto.getSubjectCourse());
        this.group = dto.getSubjectGroup();
        this.subgroup = dto.getSubjectSubgroup();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la asignatura no puede estar vacío")
    private String name;

    @NotNull(message = "El curso de la asignatura no puede estar vacío")
    private Integer course;

    @NotBlank(message = "El grupo de la asignatura no puede estar vacío")
    @Column(name = "\"group\"")
    private String group;

    @NotBlank(message = "El subgrupo de la asignatura no puede estar vacío")
    private String subgroup;

    public boolean isInDto(ReservationDto dto) {
        try {
            return dto.getSubjectName().equals(name) && Integer.parseInt(dto.getSubjectCourse()) == course && dto.getSubjectGroup().equals(group) && dto.getSubjectSubgroup().equals(subgroup);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El curso de la asignatura debe ser un número");
        }
    }
}
