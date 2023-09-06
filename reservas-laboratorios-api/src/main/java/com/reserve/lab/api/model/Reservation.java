package com.reserve.lab.api.model;

import com.reserve.lab.api.model.type.DayOfWeekType;
import com.reserve.lab.api.model.type.ScheduleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@Getter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El código de la reserva no puede estar vacío")
    private Integer publicId;
    @NotNull(message = "El tipo de reserva no puede estar vacío")
    private String type;
    @NotNull(message = "El horario no puede estar vacío")
    private String schedule;
    @ManyToOne
    @JoinColumn(name = "degree_id")
    @NotNull(message = "La titulación no puede estar vacía")
    private Degree degree;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @NotNull(message = "La asignatura no puede estar vacía")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @NotNull(message = "El semestre no puede estar vacío")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    @NotNull(message = "El nombre del profesor no puede estar vacío")
    private Professor professor;
    @ManyToOne
    @JoinColumn(name = "responsible_id")
    @NotNull(message = "El nombre del responsable no puede estar vacío")
    private Responsible responsible;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department; // departamento
    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private LocalDate startDate; // inicio
    @NotNull(message = "La fecha de fin no puede estar vacía")
    private LocalDate endDate; // fin
    @NotNull(message = "La franja horaria no puede estar vacía o tiene el formato incorrecto. Debería ser 'Dia (HH:MM - HH:MM)'")
    private DayOfWeekType day; // franja : Dia (HH:MM - HH:MM)
    @NotNull(message = "La franja horaria no puede estar vacía o tiene el formato incorrecto. Debería ser 'Dia (HH:MM - HH:MM)'")
    private LocalTime startTime; // subpart de franjas
    @NotNull(message = "La franja horaria no puede estar vacía o tiene el formato incorrecto. Debería ser 'Dia (HH:MM - HH:MM)'")
    private LocalTime endTime; // subpart de franja
    private String teachingType; // tipo - type // docencia - teaching
    private String laboratoryPreference; // aula // Por cada laboratorio hay que crear uno nuevo
    private String location; // localizacion
    private Integer studentsNumber; // num_alumnos
    private String operatingSystem; // sistema_operativo
    private String additionalEquipment; // características


    public String getDay() {
        if (day != null) {
            return day.getDisplayValue();
        }
        return null;
    }

    public DayOfWeekType getDayType() {
        return day;
    }

    public List<String> getLaboratoryPreference() {
        if (laboratoryPreference != null) {
            return List.of(laboratoryPreference.split(","));
        }
        return Collections.emptyList();
    }

    public ScheduleType getScheduleType() {
        if (schedule != null) {
            return ScheduleType.valueOf(schedule.toUpperCase());
        }
        return null;
    }
}