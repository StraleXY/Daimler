package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRideRepository extends JpaRepository<Ride, Integer> {

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :id")
    Page<Ride> findRideByPassengerId(@Param("id") Integer id, Pageable pageable);

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :id AND (r.status = 'PENDING' OR r.status = 'ACTIVE' OR r.status = 'ACCEPTED')")
    List<Ride> findPendingRideByPassengerId(@Param("id") Integer id);

    @Query("SELECT r FROM Ride r JOIN r.driver d WHERE d.id = :id AND (r.status = 'PENDING' OR r.status = 'ACTIVE' OR r.status = 'ACCEPTED')")
    List<Ride> findPendingRideByDriverId(@Param("id") Integer id);

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :id AND r.status = 'FINISHED' AND r.scheduledTimestamp BETWEEN :from AND :to")
    List<Ride> findFinishedByPassengerId(@Param("id") Integer id, @Param("from") long from, @Param("to") long to);

    @Query("SELECT r FROM Ride r JOIN r.driver d WHERE d.id = :id AND r.status = 'FINISHED' AND r.scheduledTimestamp BETWEEN :from AND :to")
    List<Ride> findFinishedByDriverId(@Param("id") Integer id, @Param("from") long from, @Param("to") long to);

    @Query("SELECT r FROM Ride r JOIN r.driver d WHERE d.id = :id AND r.status = 'REJECTED' AND r.scheduledTimestamp BETWEEN :from AND :to")
    List<Ride> findRejectedByDriverId(@Param("id") Integer id, @Param("from") long from, @Param("to") long to);

    @Query("SELECT r FROM Ride r JOIN r.driver d WHERE d.id = :id")
    Page<Ride> findByDriverId(@Param("id") Integer id, Pageable pageable);

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :id")
    Page<Ride> findByPassengerId(@Param("id") Integer id, Pageable pageable);

    List<Ride> findByStatus(String status);
}
