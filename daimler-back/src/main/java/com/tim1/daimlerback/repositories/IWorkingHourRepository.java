package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Ride;
import com.tim1.daimlerback.entities.WorkingHour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface IWorkingHourRepository extends JpaRepository<WorkingHour, Integer> {
    @Query("SELECT w FROM WorkingHour w WHERE w.driverId = :id")
    Page<WorkingHour> findWorkingHoursByDriverId(@Param("id") Integer id, Pageable pageable);

    @Query("SELECT w FROM WorkingHour w WHERE w.end is null and w.driverId = :id")
    Optional<WorkingHour> findOngoingWorkingHourByDriverId(@Param("id") Integer id);

    @Query("SELECT w FROM WorkingHour w WHERE w.driverId = :id and ((:from <= w.start and :to >= w.start) or (:from <= w.end and :to >= w.end) or (w.end is null))")
    List<WorkingHour> findWorkingHoursInRange(@Param("id") Integer id, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    Optional<WorkingHour> findById(Integer id);
}
