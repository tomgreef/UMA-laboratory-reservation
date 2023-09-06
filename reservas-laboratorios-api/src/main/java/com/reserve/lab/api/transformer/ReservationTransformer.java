package com.reserve.lab.api.transformer;

import com.reserve.lab.api.exceptions.WrongDateFormatException;
import com.reserve.lab.api.model.*;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.type.*;
import com.reserve.lab.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class ReservationTransformer {
    private final DegreeRepository degreeRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final ResponsibleRepository responsibleRepository;
    private final DepartmentRepository departmentRepository;
    private final List<Degree> degrees;
    private final List<Subject> subjects;
    private final List<Professor> professors;
    private final List<Responsible> responsibles;
    private final List<Department> departments;


    public ReservationTransformer(DegreeRepository degreeRepository, SubjectRepository subjectRepository, ProfessorRepository professorRepository, ResponsibleRepository responsibleRepository, DepartmentRepository departmentRepository) {
        this.degreeRepository = degreeRepository;
        this.subjectRepository = subjectRepository;
        this.professorRepository = professorRepository;
        this.responsibleRepository = responsibleRepository;
        this.departmentRepository = departmentRepository;
        degrees = (List<Degree>) degreeRepository.findAll();
        subjects = (List<Subject>) subjectRepository.findAll();
        professors = (List<Professor>) professorRepository.findAll();
        responsibles = (List<Responsible>) responsibleRepository.findAll();
        departments = (List<Department>) departmentRepository.findAll();
    }

    public Reservation createModelFromDto(ReservationDto dto) {
        Reservation model = new Reservation();
        try {
            model.setPublicId(Integer.parseInt(dto.getPublicId()));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El identificador de la reserva debe ser un número");
        }
        mapValuesToModel(dto, model);
        return model;
    }

    public void mapValuesToModel(ReservationDto dto, Reservation model) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            model.setStartDate(LocalDate.parse(dto.getStartDate(), formatter));
            model.setEndDate(LocalDate.parse(dto.getEndDate(), formatter));
        } catch (Exception e) {
            throw new WrongDateFormatException("El formato de la fecha '" + dto.getStartDate() + "' no es válido");
        }
        try {
            String[] dayAndTime = dto.getDayAndTimeSlot().split(" ");
            model.setDay(DayOfWeekType.fromDisplayValue(dayAndTime[0]));
            model.setStartTime(LocalTime.parse(dayAndTime[1].replace("(", "")));
            model.setEndTime(LocalTime.parse(dayAndTime[3].replace(")", "")));
        } catch (Exception e) {
            throw new WrongDateFormatException("El formato de la franja horaria '" + dto.getDayAndTimeSlot() + "' no es válido. Debe ser del tipo 'Lunes (10:00 - 12:00)'");
        }

        model.setLocation(cleanString(AnythingType.verify(dto.getLocation())));
        if (AnythingType.verify(dto.getLaboratoryPreference()) != null) {
            try {
                dto.getLaboratoryPreference().split(",");
                model.setLaboratoryPreference(cleanString(dto.getLaboratoryPreference()));
            } catch (Exception e) {
                throw new WrongDateFormatException("El formato de la lista de laboratorios '" + dto.getLaboratoryPreference() + "' no es válido. Debe ser del formato 'Laboratorio 1, Laboratorio 2, ...'");
            }
        }

        try {
            model.setStudentsNumber(Integer.parseInt(dto.getStudentsNumber()));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El número de estudiantes debe ser un número");
        }
        model.setType(ReservationType.fromDisplayValue(dto.getType()));
        model.setSchedule(ScheduleType.fromDisplayValue(dto.getSchedule()));
        model.setOperatingSystem(cleanString(dto.getOperatingSystem()));
        model.setAdditionalEquipment(cleanString(AnythingType.verify(dto.getAdditionalEquipment())));

        // Database entities
        Degree degree = degrees.stream().filter(d -> d.getName().equals(dto.getDegreeName())).findFirst().orElse(saveAndStoreEntityInList(new Degree(dto.getDegreeName()), degreeRepository, degrees));
        Subject subject = subjects.stream().filter(s -> s.isInDto(dto)).findFirst().orElse(saveAndStoreEntityInList(new Subject(dto), subjectRepository, subjects));
        Professor professor = professors.stream().filter(p -> p.isInDto(dto)).findFirst().orElse(saveAndStoreEntityInList(new Professor(dto), professorRepository, professors));
        Responsible responsible = responsibles.stream().filter(r -> r.isInDto(dto)).findFirst().orElse(saveAndStoreEntityInList(new Responsible(dto), responsibleRepository, responsibles));
        Department department = null;
        if (dto.getDepartmentName() != null && !dto.getDepartmentName().isEmpty()) {
            department = departments.stream().filter(d -> d.getName().equals(dto.getDepartmentName())).findFirst().orElse(saveAndStoreEntityInList(new Department(dto.getDepartmentName()), departmentRepository, departments));
        }

        model.setTeachingType(TeachingType.fromDisplayValue(dto.getTeachingType()));
        model.setDegree(degree);
        model.setSubject(subject);
        model.setProfessor(professor);
        model.setResponsible(responsible);
        model.setDepartment(department);
    }

    private <MODEL, REPOSITORY extends CrudRepository<MODEL, Long>> MODEL saveAndStoreEntityInList(MODEL model, REPOSITORY repository, List<MODEL> list) {
        MODEL modelWithId = repository.save(model);
        list.add(modelWithId);
        return modelWithId;
    }

    static String cleanString(String string) {
        if (string == null || string.isBlank()) return null;
        return string
                .replace("_", " ")
                .replace("-", " ")
                .replace("\"", "")
                .replace("   ", " ")
                .replace("  ", " ")
                .replace("á", "a")
                .replace("Á", "a")
                .replace("é", "e")
                .replace("É", "e")
                .replace("í", "i")
                .replace("Í", "i")
                .replace("ó", "o")
                .replace("Ó", "o")
                .replace("ú", "u")
                .replace("Ú", "u")
                .trim()
                .toUpperCase();
    }
}
