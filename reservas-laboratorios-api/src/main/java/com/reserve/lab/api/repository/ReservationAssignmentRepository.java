package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Laboratory;
import com.reserve.lab.api.model.ReservationAssignment;
import com.reserve.lab.api.model.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationAssignmentRepository extends CrudRepository<ReservationAssignment, Long> {
    @Query("SELECT ra FROM ReservationAssignment ra WHERE ra.semester = :semester ORDER BY ra.laboratory.name ASC")
    List<ReservationAssignment> findAllBySemester(Semester semester);

    void deleteAllBySemester(Semester semester);

    List<ReservationAssignment> findAllByLaboratory(Laboratory laboratory);
}
