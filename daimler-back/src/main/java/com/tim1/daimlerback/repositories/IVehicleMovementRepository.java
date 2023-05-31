package com.tim1.daimlerback.repositories;


import com.tim1.daimlerback.entities.VehicleMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVehicleMovementRepository extends JpaRepository<VehicleMovement, Integer> {
    Optional<VehicleMovement> findByRideId(Integer rideId);
}
