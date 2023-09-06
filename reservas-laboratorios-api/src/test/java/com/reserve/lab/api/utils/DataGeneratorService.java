package com.reserve.lab.api.utils;

import com.github.javafaker.Faker;
import com.reserve.lab.api.model.dto.LaboratoryDto;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.dto.SemesterDto;
import com.reserve.lab.api.model.type.ReservationType;
import com.reserve.lab.api.model.type.ScheduleType;
import com.reserve.lab.api.model.type.TeachingType;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class DataGeneratorService {
    Faker faker = new Faker(new Locale("es"));

    public SemesterDto getSemesterDto() {
        int startYear = faker.number().numberBetween(1000, 3000);
        return SemesterDto.builder()
                .startYear(startYear)
                .endYear(startYear + 1)
                .period(faker.number().numberBetween(1, 2))
                .build();
    }

    public List<LaboratoryDto> getLaboratories(int number) {
        return getLaboratories(number, null);
    }

    public List<LaboratoryDto> getLaboratories(int number, Integer capacity) {
        List<LaboratoryDto> laboratories = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            laboratories.add(getLaboratoryDto(capacity));
        }
        return laboratories;
    }

    public List<LaboratoryDto> getLaboratoriesForReservations(List<ReservationDto> reservations) {
        return reservations.stream().map(reservation -> getLaboratoryDto(Integer.valueOf(reservation.getStudentsNumber()))).toList();
    }

    private LaboratoryDto getLaboratoryDto(Integer capacity) {
        return LaboratoryDto.builder()
                .id(-1L)
                .name(faker.lordOfTheRings().character())
                .capacity(capacity != null ? capacity : faker.number().numberBetween(50, 60))
                .adjacentLaboratories(new ArrayList<>())
                .location(faker.address().fullAddress())
                .build();
    }

    public List<ReservationDto> getReservations(int number) {
        List<ReservationDto> reservations = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            reservations.add(getReservationDto(null));
        }
        return reservations;
    }

    public List<ReservationDto> getReservations(int number, List<LaboratoryDto> laboratoryPreferenceOptions) {
        List<ReservationDto> reservations = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            reservations.add(getReservationDto(laboratoryPreferenceOptions));
        }
        return reservations;
    }

    private ReservationDto getReservationDto(List<LaboratoryDto> laboratoryPreferenceOptions) {
        Date startDate = faker.date().future(50, TimeUnit.DAYS);
        Date endDate = faker.date().future(50, TimeUnit.DAYS, startDate);
        LaboratoryDto preferredLaboratory = laboratoryPreferenceOptions == null ? null : faker.options().option(laboratoryPreferenceOptions.get(faker.number().numberBetween(0, laboratoryPreferenceOptions.size())));
        String dayOfWeek = faker.options().option("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
        String timeSlot = faker.options().option("(08:45 - 10:30)", "(10:45 - 12:30)", "(12:45 - 14:30)", "(15:30 - 17:15)", "(17:30 - 19:15)", "(19:30 - 21:15)");

        // Generate the time slot in the desired format
        String dayAndTimeSlot = dayOfWeek + " " + timeSlot;

        return ReservationDto.builder()
                .publicId(faker.number().digit())
                .teachingType(TeachingType.REGULATED.getDisplayValue())
                .degreeName(faker.educator().campus())
                .subjectName(faker.educator().course())
                .subjectCourse(faker.number().numberBetween(1, 4) + "")
                .subjectGroup(faker.number().numberBetween(1, 4) + "")
                .subjectSubgroup(faker.number().numberBetween(1, 4) + "")
                .professorName(faker.name().fullName())
                .professorEmail(faker.internet().emailAddress())
                .responsibleName(faker.name().fullName())
                .responsiblePhone(faker.phoneNumber().cellPhone())
                .departmentName(faker.educator().university())
                .startDate(formatDate(startDate))
                .endDate(formatDate(endDate))
                .dayAndTimeSlot(dayAndTimeSlot)
                .studentsNumber(faker.number().numberBetween(1, 50) + "")
                .type(ReservationType.WEEKLY.getDisplayValue())
                .schedule(ScheduleType.PREFERRED.getDisplayValue())
                .laboratoryPreference(preferredLaboratory != null ? preferredLaboratory.getName() : null)
                .location(preferredLaboratory != null ? preferredLaboratory.getLocation() : null)
                .build();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        return sdf.format(date);
    }
}
