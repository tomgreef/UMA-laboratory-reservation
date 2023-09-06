package com.reserve.lab.api.repository;

import com.reserve.lab.api.model.Reservation;
import com.reserve.lab.api.model.Semester;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    List<Reservation> findAllBySemester(Semester semester);

    @Query("SELECT r FROM Reservation r WHERE r.semester.id = :semesterId ORDER BY r.id ASC")
    List<Reservation> findAllBySemesterId(Long semesterId);

    void deleteAllBySemester(Semester semester);
}
