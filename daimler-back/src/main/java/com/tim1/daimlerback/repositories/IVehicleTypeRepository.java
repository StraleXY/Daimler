package com.tim1.daimlerback.repositories;

import com.tim1.daimlerback.entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IVehicleTypeRepository extends JpaRepository<VehicleType, Integer> {
    public Optional<VehicleType> findByName(String name);
}
