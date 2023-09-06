package com.reserve.lab.api.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDto {
    private String publicId;
    private String teachingType;
    private String degreeName;
    private String subjectName;
    private String subjectCourse;
    private String subjectGroup;
    private String subjectSubgroup;
    private String professorName;
    private String professorEmail;
    private String departmentName;
    private String startDate;
    private String endDate;
    private String dayAndTimeSlot;
    private String location;
    private String laboratoryPreference;
    private String studentsNumber;
    private String type;
    private String responsibleName;
    private String responsiblePhone;
    private String schedule; // Or publicId in case of reservation of type CANCELACION_SOLICITUD
    private String operatingSystem;
    private String additionalEquipment;
}
