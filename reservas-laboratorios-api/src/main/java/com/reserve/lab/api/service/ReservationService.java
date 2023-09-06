package com.reserve.lab.api.service;

import com.reserve.lab.api.model.Reservation;
import com.reserve.lab.api.model.Semester;
import com.reserve.lab.api.model.dto.ReservationDto;
import com.reserve.lab.api.model.dto.ReservationDtoWithError;
import com.reserve.lab.api.model.type.ReservationType;
import com.reserve.lab.api.repository.ReservationRepository;
import com.reserve.lab.api.transformer.ReservationTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {
    public static final int ROW_NUMBER_OFFSET = 2;
    private final ReservationRepository repository;
    private final ReservationTransformer transformer;
    private final TaskService taskService;
    private final ReservationAssignmentService reservationAssignmentService;
    private final ReservationConflictService reservationConflictService;
    private final LaboratoryService laboratoryService;

    public List<ReservationDtoWithError> saveExcel(List<ReservationDto> dtoList, Semester semester) {
        List<Reservation> modelsToSave = new ArrayList<>();
        List<ReservationDtoWithError> dtoWithErrors = new ArrayList<>();
        Set<Integer> publicIdsToDelete = new HashSet<>();
        AtomicInteger index = new AtomicInteger(0);

        dtoList.forEach(dto -> {
            int currentIndex = index.getAndIncrement();
            try {
                if (ReservationType.CANCELLATION.getDisplayValue().equals(dto.getType())) {
                    // In case of cancellation, delete the reservations. The code ofm the reservation is in the same column as the schedule
                    try {
                        publicIdsToDelete.add(Integer.parseInt(dto.getSchedule()));
                    } catch (Exception e) {
                        log.error("Error parsing the reservation code to delete");
                        throw new IllegalArgumentException("Error al parsear el código de la reserva a eliminar, asegurate de que el código de la reserva es un número entero en la columna 'Horario'");
                    }
                } else {
                    Reservation model = transformer.createModelFromDto(dto);
                    model.setSemester(semester);
                    modelsToSave.add(model);
                }
            } catch (Exception e) {
                dtoWithErrors.add(new ReservationDtoWithError(dto, currentIndex + ROW_NUMBER_OFFSET /* Start from 0 and increment because of header */, e.getMessage()));
            }
        });

        if (dtoWithErrors.isEmpty() && !modelsToSave.isEmpty()) {
            List<Reservation> savedModels = (List<Reservation>) repository.saveAll(modelsToSave.stream().filter(model ->
                    !publicIdsToDelete.contains(model.getPublicId())
            ).toList());

            laboratoryService.upsert(savedModels.stream().filter(reservation -> reservation.getLaboratoryPreference() != null).toList());
            taskService.createTask(semester);
        }

        return dtoWithErrors;
    }

    public List<Reservation> findAllBySemester(Semester semester) {
        return repository.findAllBySemester(semester);
    }

    public List<Reservation> findAllBySemester(Long semesterId) {
        return repository.findAllBySemesterId(semesterId);
    }

    @Transactional
    public void deleteAllBySemester(Semester semester) {
        reservationAssignmentService.deleteAllBySemester(semester);
        reservationConflictService.deleteAllBySemester(semester);
        repository.deleteAllBySemester(semester);
    }
}
