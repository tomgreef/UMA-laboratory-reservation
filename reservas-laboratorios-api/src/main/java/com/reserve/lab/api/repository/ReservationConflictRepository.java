package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.ReservationConflict;
import com.reserve.lab.api.model.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationConflictRepository extends CrudRepository<ReservationConflict, Long> {
    @Query("SELECT rc FROM ReservationConflict rc WHERE rc.semester = :semester ORDER BY rc.laboratory.name ASC")
    List<ReservationConflict> findAllBySemester(Semester semester);
}
